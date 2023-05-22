package com.story.platform.core.domain.post

import com.story.platform.core.common.enums.CursorDirection
import com.story.platform.core.common.error.NotFoundException
import com.story.platform.core.common.model.Cursor
import com.story.platform.core.common.model.CursorRequest
import com.story.platform.core.common.model.CursorResult
import org.springframework.data.cassandra.core.query.CassandraPageRequest
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service

@Service
class PostRetriever(
    private val postRepository: PostRepository,
    private val postIdGenerator: PostIdGenerator,
) {

    suspend fun findPost(
        postSpaceKey: PostSpaceKey,
        postId: String,
    ): Post {
        return postRepository.findByKeyServiceTypeAndKeySpaceTypeAndKeySpaceIdAndKeySlotIdAndKeyPostId(
            serviceType = postSpaceKey.serviceType,
            spaceType = postSpaceKey.spaceType,
            spaceId = postSpaceKey.spaceId,
            slotId = PostSlotAssigner.assign(postId),
            postId = postId,
        ) ?: throw NotFoundException("해당하는 Space($postSpaceKey)에 포스트($postId)가 존재하지 않습니다")
    }

    // TDOO: 멀티 슬롯 변경해가면서 페이징 하도록 변경 필요
    suspend fun findPosts(
        postSpaceKey: PostSpaceKey,
        cursorRequest: CursorRequest,
    ): CursorResult<Post, String> {
        val posts = when (cursorRequest.direction) {
            CursorDirection.NEXT -> getNextPosts(cursorRequest, postSpaceKey)
            CursorDirection.PREVIOUS -> getPreviousPosts(cursorRequest, postSpaceKey)
        }
        return CursorResult(
            data = posts.content,
            cursor = getCursor(posts),
        )
    }

    private suspend fun getNextPosts(
        cursorRequest: CursorRequest,
        postSpaceKey: PostSpaceKey,
    ): Slice<Post> {
        if (cursorRequest.cursor == null) {
            val lastSlotId =
                PostSlotAssigner.assign(postId = postIdGenerator.getLastPostId(postSpaceKey = postSpaceKey))
            return postRepository.findAllByKeyServiceTypeAndKeySpaceTypeAndKeySpaceIdAndKeySlotId(
                serviceType = postSpaceKey.serviceType,
                spaceType = postSpaceKey.spaceType,
                spaceId = postSpaceKey.spaceId,
                slotId = lastSlotId,
                pageable = CassandraPageRequest.first(cursorRequest.pageSize),
            )
        }

        val currentSlot = PostSlotAssigner.assign(postId = cursorRequest.cursor)
        return postRepository.findAllByKeyServiceTypeAndKeySpaceTypeAndKeySpaceIdAndKeySlotIdAndKeyPostIdLessThan(
            serviceType = postSpaceKey.serviceType,
            spaceType = postSpaceKey.spaceType,
            spaceId = postSpaceKey.spaceId,
            slotId = currentSlot,
            pageable = CassandraPageRequest.first(cursorRequest.pageSize),
            postId = cursorRequest.cursor,
        )
    }

    private suspend fun getPreviousPosts(
        cursorRequest: CursorRequest,
        postSpaceKey: PostSpaceKey,
    ): Slice<Post> {
        if (cursorRequest.cursor == null) {
            val firstSlotId = PostSlotAssigner.FIRST_SLOT_ID
            return postRepository.findAllByKeyServiceTypeAndKeySpaceTypeAndKeySpaceIdAndKeySlotIdOrderByKeyPostIdAsc(
                serviceType = postSpaceKey.serviceType,
                spaceType = postSpaceKey.spaceType,
                spaceId = postSpaceKey.spaceId,
                slotId = firstSlotId,
                pageable = CassandraPageRequest.first(cursorRequest.pageSize),
            )
        }
        val currentSlot = PostSlotAssigner.assign(postId = cursorRequest.cursor)
        return postRepository.findAllByKeyServiceTypeAndKeySpaceTypeAndKeySpaceIdAndKeySlotIdAndKeyPostIdGreaterThanOrderByKeyPostIdAsc(
            serviceType = postSpaceKey.serviceType,
            spaceType = postSpaceKey.spaceType,
            spaceId = postSpaceKey.spaceId,
            slotId = currentSlot,
            postId = cursorRequest.cursor,
            pageable = CassandraPageRequest.first(cursorRequest.pageSize),
        )
    }

    private fun getCursor(posts: Slice<Post>): Cursor<String> {
        if (posts.hasNext()) {
            return Cursor(cursor = posts.content.last().key.postId)
        }
        return Cursor(cursor = null)
    }

}
