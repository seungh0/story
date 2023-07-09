package com.story.platform.core.domain.post

import com.story.platform.core.common.enums.CursorDirection
import com.story.platform.core.common.error.InvalidCursorException
import com.story.platform.core.common.model.Cursor
import com.story.platform.core.common.model.CursorRequest
import com.story.platform.core.common.model.CursorResult
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.toList
import org.springframework.data.cassandra.core.query.CassandraPageRequest
import org.springframework.stereotype.Service

@Service
class PostRetriever(
    private val postRepository: PostRepository,
    private val postSequenceGenerator: PostSequenceGenerator,
) {

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
        workspaceId: String,
        componentId: String,
        keys: Collection<PostKey>,
    ): List<PostResponse> = coroutineScope {
        val posts = keys.map { key ->
            async {
                postRepository.findById(
                    PostPrimaryKey.of(
                        postSpaceKey = PostSpaceKey(
                            workspaceId = workspaceId,
                            componentId = componentId,
                            spaceId = key.spaceId,
                        ),
                        postId = key.postId,
                    )
                )
            }
        }
        return@coroutineScope posts.awaitAll().filterNotNull()
            .map { post -> PostResponse.of(post) }
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
                cursor = getCursor(posts = posts, pageSize = cursorRequest.pageSize),
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
            cursor = getCursor(posts = morePosts, pageSize = cursorRequest.pageSize - posts.size),
        )
    }

    private suspend fun listNextPosts(
        cursorRequest: CursorRequest,
        postSpaceKey: PostSpaceKey,
    ): Pair<Long, List<Post>> {
        if (cursorRequest.cursor == null) {
            val lastSlotId =
                PostSlotAssigner.assign(postId = postSequenceGenerator.lastSequence(postSpaceKey = postSpaceKey))
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

    private fun getCursor(posts: List<Post>, pageSize: Int): Cursor<String> {
        if (posts.size > pageSize) {
            return Cursor.of(
                cursor = posts.subList(0, pageSize.coerceAtMost(posts.size)).lastOrNull()?.key?.postId?.toString()
            )
        }
        return Cursor.of(cursor = null)
    }

}
