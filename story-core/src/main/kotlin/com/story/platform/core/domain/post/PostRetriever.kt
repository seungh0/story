package com.story.platform.core.domain.post

import com.story.platform.core.common.error.InvalidCursorException
import com.story.platform.core.common.model.CursorDirection
import com.story.platform.core.common.model.CursorResult
import com.story.platform.core.common.model.CursorUtils
import com.story.platform.core.common.model.dto.CursorRequest
import com.story.platform.core.infrastructure.cache.CacheType
import com.story.platform.core.infrastructure.cache.Cacheable
import kotlinx.coroutines.flow.toList
import org.springframework.data.cassandra.core.query.CassandraPageRequest
import org.springframework.stereotype.Service

@Service
class PostRetriever(
    private val postRepository: PostRepository,
    private val postSequenceRepository: PostSequenceRepository,
) {

    @Cacheable(
        cacheType = CacheType.POST,
        key = "'workspaceId:' + {#postSpaceKey.workspaceId} + ':componentId:' + {#postSpaceKey.componentId} + ':spaceId:' + {#postSpaceKey.spaceId} + ':postId:' + {#postId}",
    )
    suspend fun getPost(
        postSpaceKey: PostSpaceKey,
        postId: Long,
    ): PostResponse {
        return PostResponse.of(
            postRepository.findByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeySlotIdAndKeyPostId(
                workspaceId = postSpaceKey.workspaceId,
                componentId = postSpaceKey.componentId,
                spaceId = postSpaceKey.spaceId,
                slotId = PostSlotAssigner.assign(postId),
                postId = postId,
            ) ?: throw PostNotExistsException(message = "해당하는 Space($postSpaceKey)에 포스트($postId)가 존재하지 않습니다")
        )
    }

    suspend fun listPosts(
        postSpaceKey: PostSpaceKey,
        cursorRequest: CursorRequest,
    ): CursorResult<PostResponse, String> {
        val (slot: Long, posts: List<Post>) = when (cursorRequest.direction) {
            CursorDirection.NEXT -> listNextPosts(cursorRequest, postSpaceKey)
            CursorDirection.PREVIOUS -> listPreviousPosts(cursorRequest, postSpaceKey)
        }

        if (posts.size > cursorRequest.pageSize) {
            return CursorResult(
                data = posts.subList(0, cursorRequest.pageSize.coerceAtMost(posts.size))
                    .map { post -> PostResponse.of(post) },
                cursor = CursorUtils.getCursor(
                    listWithNextCursor = posts,
                    pageSize = cursorRequest.pageSize,
                    keyGenerator = { post -> post?.key?.postId?.toString() }
                )
            )
        }

        val morePosts = when (cursorRequest.direction) {
            CursorDirection.NEXT -> {
                postRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeySlotId(
                    workspaceId = postSpaceKey.workspaceId,
                    componentId = postSpaceKey.componentId,
                    spaceId = postSpaceKey.spaceId,
                    slotId = slot - 1,
                    pageable = CassandraPageRequest.first(cursorRequest.pageSize - posts.size + 1),
                ).toList()
            }

            CursorDirection.PREVIOUS -> {
                postRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeySlotIdOrderByKeyPostIdAsc(
                    workspaceId = postSpaceKey.workspaceId,
                    componentId = postSpaceKey.componentId,
                    spaceId = postSpaceKey.spaceId,
                    slotId = slot + 1,
                    pageable = CassandraPageRequest.first(cursorRequest.pageSize - posts.size + 1),
                ).toList()
            }
        }

        val data = posts + morePosts.subList(0, (cursorRequest.pageSize - posts.size).coerceAtMost(morePosts.size))

        return CursorResult(
            data = data.map { post -> PostResponse.of(post) },
            cursor = CursorUtils.getCursor(
                listWithNextCursor = morePosts,
                pageSize = cursorRequest.pageSize - posts.size,
                keyGenerator = { post -> post?.key?.postId?.toString() }
            )
        )
    }

    private suspend fun listNextPosts(
        cursorRequest: CursorRequest,
        postSpaceKey: PostSpaceKey,
    ): Pair<Long, List<Post>> {
        if (cursorRequest.cursor == null) {
            val lastSlotId = PostSlotAssigner.assign(postId = postSequenceRepository.lastSequence(postSpaceKey = postSpaceKey))
            return lastSlotId to postRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeySlotId(
                workspaceId = postSpaceKey.workspaceId,
                componentId = postSpaceKey.componentId,
                spaceId = postSpaceKey.spaceId,
                slotId = lastSlotId,
                pageable = CassandraPageRequest.first(cursorRequest.pageSize + 1),
            ).toList()
        }

        val currentSlot = PostSlotAssigner.assign(
            postId = cursorRequest.cursor.toLongOrNull()
                ?: throw InvalidCursorException("잘못된 Cursor(${cursorRequest.cursor})입니다")
        )
        return currentSlot to postRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeySlotIdAndKeyPostIdLessThan(
            workspaceId = postSpaceKey.workspaceId,
            componentId = postSpaceKey.componentId,
            spaceId = postSpaceKey.spaceId,
            slotId = currentSlot,
            pageable = CassandraPageRequest.first(cursorRequest.pageSize + 1),
            postId = cursorRequest.cursor.toLongOrNull()
                ?: throw InvalidCursorException("잘못된 Cursor(${cursorRequest.cursor})입니다"),
        ).toList()
    }

    private suspend fun listPreviousPosts(
        cursorRequest: CursorRequest,
        postSpaceKey: PostSpaceKey,
    ): Pair<Long, List<Post>> {
        if (cursorRequest.cursor == null) {
            val firstSlotId = PostSlotAssigner.FIRST_SLOT_ID
            return firstSlotId to postRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeySlotIdOrderByKeyPostIdAsc(
                workspaceId = postSpaceKey.workspaceId,
                componentId = postSpaceKey.componentId,
                spaceId = postSpaceKey.spaceId,
                slotId = firstSlotId,
                pageable = CassandraPageRequest.first(cursorRequest.pageSize + 1),
            ).toList()
        }
        val currentSlot = PostSlotAssigner.assign(
            postId = cursorRequest.cursor.toLongOrNull()
                ?: throw InvalidCursorException("잘못된 Cursor(${cursorRequest.cursor})입니다"),
        )
        return currentSlot to postRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeySlotIdAndKeyPostIdGreaterThanOrderByKeyPostIdAsc(
            workspaceId = postSpaceKey.workspaceId,
            componentId = postSpaceKey.componentId,
            spaceId = postSpaceKey.spaceId,
            slotId = currentSlot,
            postId = cursorRequest.cursor.toLongOrNull()
                ?: throw InvalidCursorException("잘못된 Cursor(${cursorRequest.cursor})입니다"),
            pageable = CassandraPageRequest.first(cursorRequest.pageSize + 1),
        ).toList()
    }

}
