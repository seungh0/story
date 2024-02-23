package com.story.core.domain.post

import com.story.core.common.error.InvalidCursorException
import com.story.core.common.error.NotSupportedException
import com.story.core.common.model.CursorDirection
import com.story.core.common.model.Slice
import com.story.core.common.model.dto.CursorRequest
import com.story.core.common.utils.CursorUtils
import com.story.core.domain.post.section.PostSectionManager
import com.story.core.domain.post.section.PostSectionRepository
import com.story.core.domain.post.section.PostSectionSlotAssigner
import com.story.core.infrastructure.cache.CacheType
import com.story.core.infrastructure.cache.Cacheable
import kotlinx.coroutines.flow.toList
import org.apache.commons.lang3.StringUtils
import org.springframework.data.cassandra.core.query.CassandraPageRequest
import org.springframework.stereotype.Service

@Service
class PostRetriever(
    private val postRepository: PostRepository,
    private val postSequenceRepository: PostSequenceRepository,
    private val postSectionRepository: PostSectionRepository,
    private val postSectionManager: PostSectionManager,
) {

    @Cacheable(
        cacheType = CacheType.POST,
        key = "'workspaceId:' + {#postSpaceKey.workspaceId} + ':componentId:' + {#postSpaceKey.componentId} + ':spaceId:' + {#postSpaceKey.spaceId} + ':parentId:' + {#postId.parentId} + ':postId:' + {#postId.postId}",
    )
    suspend fun getPost(
        postSpaceKey: PostSpaceKey,
        postId: PostKey,
    ): PostResponse {
        val post = postRepository.findByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotIdAndKeyPostId(
            workspaceId = postSpaceKey.workspaceId,
            componentId = postSpaceKey.componentId,
            spaceId = postSpaceKey.spaceId,
            parentId = postId.parentId ?: StringUtils.EMPTY,
            slotId = PostSlotAssigner.assign(postId.postId),
            postId = postId.postId,
        ) ?: throw PostNotExistsException(message = "해당하는 Space($postSpaceKey)에 포스트($postId)가 존재하지 않습니다")

        val sections = postSectionRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotIdAndKeyPostId(
            workspaceId = postSpaceKey.workspaceId,
            componentId = postSpaceKey.componentId,
            spaceId = postSpaceKey.spaceId,
            parentId = postId.parentId ?: StringUtils.EMPTY,
            slotId = PostSectionSlotAssigner.assign(postId.postId),
            postId = postId.postId,
        )

        return PostResponse.of(
            post = post,
            sections = postSectionManager.makePostSectionContentResponse(sections.toList())
        )
    }

    suspend fun listPosts(
        postSpaceKey: PostSpaceKey,
        parentId: PostKey?,
        cursorRequest: CursorRequest,
        sortBy: PostSortBy,
    ): Slice<PostResponse, String> {
        val (slot: Long, posts: List<Post>) = when (sortBy to cursorRequest.direction) {
            PostSortBy.LATEST to CursorDirection.NEXT, PostSortBy.OLDEST to CursorDirection.PREVIOUS -> listNextPosts(
                cursorRequest = cursorRequest,
                postSpaceKey = postSpaceKey,
                parentId = parentId,
            )

            PostSortBy.LATEST to CursorDirection.PREVIOUS, PostSortBy.OLDEST to CursorDirection.NEXT -> listPreviousPosts(
                cursorRequest = cursorRequest,
                postSpaceKey = postSpaceKey,
                parentId = parentId,
            )

            else -> throw NotSupportedException("지원하지 않는 SortBy($sortBy)-Direction(${cursorRequest.direction}) 입니다")
        }

        if (posts.size > cursorRequest.pageSize) {
            val postSections = posts.subList(0, cursorRequest.pageSize.coerceAtMost(posts.size))
                .groupBy { post -> PostSlotAssigner.assign(postId = post.key.postId) }
                .flatMap { (slotId, posts) ->
                    postSectionRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotIdAndKeyPostIdIn(
                        workspaceId = postSpaceKey.workspaceId,
                        componentId = postSpaceKey.componentId,
                        spaceId = postSpaceKey.spaceId,
                        parentId = parentId?.serialize() ?: StringUtils.EMPTY,
                        slotId = slotId,
                        postIds = posts.map { post -> post.key.postId },
                    ).toList()
                }.groupBy { postSection -> postSection.key.postId }

            return Slice(
                data = posts.subList(0, cursorRequest.pageSize.coerceAtMost(posts.size))
                    .map { post ->
                        PostResponse.of(
                            post = post,
                            sections = postSectionManager.makePostSectionContentResponse(
                                postSections[post.key.postId] ?: emptyList()
                            )
                        )
                    },
                cursor = CursorUtils.getCursor(
                    listWithNextCursor = posts,
                    pageSize = cursorRequest.pageSize,
                    keyGenerator = { post -> post?.key?.postId?.toString() }
                )
            )
        }

        val morePosts = when (cursorRequest.direction) {
            CursorDirection.NEXT -> {
                postRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotId(
                    workspaceId = postSpaceKey.workspaceId,
                    componentId = postSpaceKey.componentId,
                    spaceId = postSpaceKey.spaceId,
                    parentId = parentId?.serialize() ?: StringUtils.EMPTY,
                    slotId = slot - 1,
                    pageable = CassandraPageRequest.first(cursorRequest.pageSize - posts.size + 1),
                ).toList()
            }

            CursorDirection.PREVIOUS -> {
                postRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotIdOrderByKeyPostIdAsc(
                    workspaceId = postSpaceKey.workspaceId,
                    componentId = postSpaceKey.componentId,
                    spaceId = postSpaceKey.spaceId,
                    parentId = parentId?.serialize() ?: StringUtils.EMPTY,
                    slotId = slot + 1,
                    pageable = CassandraPageRequest.first(cursorRequest.pageSize - posts.size + 1),
                ).toList()
            }
        }

        val data = posts + morePosts.subList(0, (cursorRequest.pageSize - posts.size).coerceAtMost(morePosts.size))

        val postSections = data.subList(0, cursorRequest.pageSize.coerceAtMost(posts.size))
            .groupBy { post -> PostSlotAssigner.assign(postId = post.key.postId) }
            .flatMap { (slotId, posts) ->
                postSectionRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotIdAndKeyPostIdIn(
                    workspaceId = postSpaceKey.workspaceId,
                    componentId = postSpaceKey.componentId,
                    spaceId = postSpaceKey.spaceId,
                    parentId = parentId?.serialize() ?: StringUtils.EMPTY,
                    slotId = slotId,
                    postIds = posts.map { post -> post.key.postId },
                ).toList()
            }.groupBy { postSection -> postSection.key.postId }

        return Slice(
            data = data.map { post ->
                PostResponse.of(
                    post = post,
                    sections = postSectionManager.makePostSectionContentResponse(
                        postSections[post.key.postId] ?: emptyList()
                    )
                )
            },
            cursor = CursorUtils.getCursor(
                listWithNextCursor = morePosts,
                pageSize = cursorRequest.pageSize - posts.size,
                keyGenerator = { post -> post?.key?.postId?.toString() }
            )
        )
    }

    private suspend fun listNextPosts(
        cursorRequest: CursorRequest,
        parentId: PostKey?,
        postSpaceKey: PostSpaceKey,
    ): Pair<Long, List<Post>> {
        if (cursorRequest.cursor == null) {
            val lastSlotId = PostSlotAssigner.assign(
                postId = postSequenceRepository.getLastSequence(
                    postSpaceKey = postSpaceKey,
                    parentId = parentId
                )
            )
            return lastSlotId to postRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotId(
                workspaceId = postSpaceKey.workspaceId,
                componentId = postSpaceKey.componentId,
                spaceId = postSpaceKey.spaceId,
                parentId = parentId?.serialize() ?: StringUtils.EMPTY,
                slotId = lastSlotId,
                pageable = CassandraPageRequest.first(cursorRequest.pageSize + 1),
            ).toList()
        }

        val currentSlot = PostSlotAssigner.assign(
            postId = cursorRequest.cursor.toLongOrNull()
                ?: throw InvalidCursorException("잘못된 CursorResponse(${cursorRequest.cursor})입니다")
        )
        return currentSlot to postRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotIdAndKeyPostIdLessThan(
            workspaceId = postSpaceKey.workspaceId,
            componentId = postSpaceKey.componentId,
            spaceId = postSpaceKey.spaceId,
            parentId = parentId?.serialize() ?: StringUtils.EMPTY,
            slotId = currentSlot,
            pageable = CassandraPageRequest.first(cursorRequest.pageSize + 1),
            postId = cursorRequest.cursor.toLongOrNull()
                ?: throw InvalidCursorException("잘못된 CursorResponse(${cursorRequest.cursor})입니다"),
        ).toList()
    }

    private suspend fun listPreviousPosts(
        cursorRequest: CursorRequest,
        parentId: PostKey?,
        postSpaceKey: PostSpaceKey,
    ): Pair<Long, List<Post>> {
        if (cursorRequest.cursor == null) {
            val firstSlotId = PostSlotAssigner.FIRST_SLOT_ID
            return firstSlotId to postRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotIdOrderByKeyPostIdAsc(
                workspaceId = postSpaceKey.workspaceId,
                componentId = postSpaceKey.componentId,
                spaceId = postSpaceKey.spaceId,
                parentId = parentId?.serialize() ?: StringUtils.EMPTY,
                slotId = firstSlotId,
                pageable = CassandraPageRequest.first(cursorRequest.pageSize + 1),
            ).toList()
        }
        val currentSlot = PostSlotAssigner.assign(
            postId = cursorRequest.cursor.toLongOrNull()
                ?: throw InvalidCursorException("잘못된 CursorResponse(${cursorRequest.cursor})입니다"),
        )
        return currentSlot to postRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotIdAndKeyPostIdGreaterThanOrderByKeyPostIdAsc(
            workspaceId = postSpaceKey.workspaceId,
            componentId = postSpaceKey.componentId,
            spaceId = postSpaceKey.spaceId,
            parentId = parentId?.serialize() ?: StringUtils.EMPTY,
            slotId = currentSlot,
            postId = cursorRequest.cursor.toLongOrNull()
                ?: throw InvalidCursorException("잘못된 CursorResponse(${cursorRequest.cursor})입니다"),
            pageable = CassandraPageRequest.first(cursorRequest.pageSize + 1),
        ).toList()
    }

}
