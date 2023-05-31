package com.story.platform.core.domain.post

import com.story.platform.core.common.enums.CursorDirection
import com.story.platform.core.common.error.BadRequestException
import com.story.platform.core.common.error.NotFoundException
import com.story.platform.core.common.model.Cursor
import com.story.platform.core.common.model.CursorRequest
import com.story.platform.core.common.model.CursorResult
import org.springframework.data.cassandra.core.query.CassandraPageRequest
import org.springframework.stereotype.Service

@Service
class PostRetriever(
    private val postRepository: PostRepository,
    private val postIdGenerator: PostIdGenerator,
) {

    suspend fun getPost(
        postSpaceKey: PostSpaceKey,
        postId: Long,
    ): Post {
        return postRepository.findByKeyServiceTypeAndKeySpaceTypeAndKeySpaceIdAndKeySlotIdAndKeyPostId(
            serviceType = postSpaceKey.serviceType,
            spaceType = postSpaceKey.spaceType,
            spaceId = postSpaceKey.spaceId,
            slotId = PostSlotAssigner.assign(postId),
            postId = postId,
        ) ?: throw NotFoundException("해당하는 Space($postSpaceKey)에 포스트($postId)가 존재하지 않습니다")
    }

    suspend fun listPosts(
        postSpaceKey: PostSpaceKey,
        cursorRequest: CursorRequest,
    ): CursorResult<Post, String> {
        val (posts, slot: Long) = when (cursorRequest.direction) {
            CursorDirection.NEXT -> getNextPosts(cursorRequest, postSpaceKey)
            CursorDirection.PREVIOUS -> getPreviousPosts(cursorRequest, postSpaceKey)
        }

        if (posts.size > cursorRequest.pageSize) {
            return CursorResult(
                data = posts.subList(0, cursorRequest.pageSize.coerceAtMost(posts.size)),
                cursor = getCursor(posts = posts, pageSize = cursorRequest.pageSize),
            )
        }

        val morePosts = when (cursorRequest.direction) {
            CursorDirection.NEXT -> {
                postRepository.findAllByKeyServiceTypeAndKeySpaceTypeAndKeySpaceIdAndKeySlotId(
                    serviceType = postSpaceKey.serviceType,
                    spaceType = postSpaceKey.spaceType,
                    spaceId = postSpaceKey.spaceId,
                    slotId = slot - 1,
                    pageable = CassandraPageRequest.first(cursorRequest.pageSize - posts.size + 1),
                ).content
            }

            CursorDirection.PREVIOUS -> {
                postRepository.findAllByKeyServiceTypeAndKeySpaceTypeAndKeySpaceIdAndKeySlotIdOrderByKeyPostIdAsc(
                    serviceType = postSpaceKey.serviceType,
                    spaceType = postSpaceKey.spaceType,
                    spaceId = postSpaceKey.spaceId,
                    slotId = slot + 1,
                    pageable = CassandraPageRequest.first(cursorRequest.pageSize - posts.size + 1),
                ).content
            }
        }

        val data = posts + morePosts.subList(0, (cursorRequest.pageSize - posts.size).coerceAtMost(morePosts.size))

        return CursorResult(
            data = data,
            cursor = getCursor(posts = morePosts, pageSize = cursorRequest.pageSize - posts.size),
        )
    }

    private suspend fun getNextPosts(
        cursorRequest: CursorRequest,
        postSpaceKey: PostSpaceKey,
    ): Pair<List<Post>, Long> {
        if (cursorRequest.cursor == null) {
            val lastSlotId =
                PostSlotAssigner.assign(postId = postIdGenerator.getLastPostId(postSpaceKey = postSpaceKey))
            return postRepository.findAllByKeyServiceTypeAndKeySpaceTypeAndKeySpaceIdAndKeySlotId(
                serviceType = postSpaceKey.serviceType,
                spaceType = postSpaceKey.spaceType,
                spaceId = postSpaceKey.spaceId,
                slotId = lastSlotId,
                pageable = CassandraPageRequest.first(cursorRequest.pageSize + 1),
            ).content to lastSlotId
        }

        val currentSlot = PostSlotAssigner.assign(
            postId = cursorRequest.cursor.toLongOrNull()
                ?: throw BadRequestException("잘못된 Cursor(${cursorRequest.cursor})입니다")
        )
        return postRepository.findAllByKeyServiceTypeAndKeySpaceTypeAndKeySpaceIdAndKeySlotIdAndKeyPostIdLessThan(
            serviceType = postSpaceKey.serviceType,
            spaceType = postSpaceKey.spaceType,
            spaceId = postSpaceKey.spaceId,
            slotId = currentSlot,
            pageable = CassandraPageRequest.first(cursorRequest.pageSize + 1),
            postId = cursorRequest.cursor.toLongOrNull()
                ?: throw BadRequestException("잘못된 Cursor(${cursorRequest.cursor})입니다"),
        ).content to currentSlot
    }

    private suspend fun getPreviousPosts(
        cursorRequest: CursorRequest,
        postSpaceKey: PostSpaceKey,
    ): Pair<List<Post>, Long> {
        if (cursorRequest.cursor == null) {
            val firstSlotId = PostSlotAssigner.FIRST_SLOT_ID
            return postRepository.findAllByKeyServiceTypeAndKeySpaceTypeAndKeySpaceIdAndKeySlotIdOrderByKeyPostIdAsc(
                serviceType = postSpaceKey.serviceType,
                spaceType = postSpaceKey.spaceType,
                spaceId = postSpaceKey.spaceId,
                slotId = firstSlotId,
                pageable = CassandraPageRequest.first(cursorRequest.pageSize + 1),
            ).content to firstSlotId
        }
        val currentSlot = PostSlotAssigner.assign(
            postId = cursorRequest.cursor.toLongOrNull()
                ?: throw BadRequestException("잘못된 Cursor(${cursorRequest.cursor})입니다"),
        )
        return postRepository.findAllByKeyServiceTypeAndKeySpaceTypeAndKeySpaceIdAndKeySlotIdAndKeyPostIdGreaterThanOrderByKeyPostIdAsc(
            serviceType = postSpaceKey.serviceType,
            spaceType = postSpaceKey.spaceType,
            spaceId = postSpaceKey.spaceId,
            slotId = currentSlot,
            postId = cursorRequest.cursor.toLongOrNull()
                ?: throw BadRequestException("잘못된 Cursor(${cursorRequest.cursor})입니다"),
            pageable = CassandraPageRequest.first(cursorRequest.pageSize + 1),
        ).content to currentSlot
    }

    private fun getCursor(posts: List<Post>, pageSize: Int): Cursor<String> {
        if (posts.size > pageSize) {
            return Cursor.of(
                cursor = posts.subList(0, pageSize.coerceAtMost(posts.size)).lastOrNull()?.key?.postId?.toString()
            )
        }
        return Cursor.of(cursor = null)
    }

}
