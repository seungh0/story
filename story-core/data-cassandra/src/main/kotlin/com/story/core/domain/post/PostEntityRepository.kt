package com.story.core.domain.post

import com.story.core.common.distribution.DistributionKey
import com.story.core.common.error.InvalidCursorException
import com.story.core.common.error.NoPermissionException
import com.story.core.common.error.NotSupportedException
import com.story.core.common.model.CursorDirection
import com.story.core.common.model.Slice
import com.story.core.common.model.dto.CursorRequest
import com.story.core.common.utils.CursorUtils
import com.story.core.domain.post.section.PostSectionCassandraRepository
import com.story.core.domain.post.section.PostSectionContentCommand
import com.story.core.domain.post.section.PostSectionEntity
import com.story.core.domain.post.section.PostSectionManager
import com.story.core.domain.post.section.PostSectionPartitionKey
import com.story.core.domain.post.section.PostSectionSlotAssigner
import com.story.core.support.cassandra.executeCoroutine
import com.story.core.support.cassandra.upsert
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.toList
import org.apache.commons.lang3.StringUtils
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.data.cassandra.core.query.CassandraPageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class PostEntityRepository(
    private val postCassandraRepository: PostCassandraRepository,
    private val postSectionCassandraRepository: PostSectionCassandraRepository,
    private val postSectionManager: PostSectionManager,
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
    private val postReverseCassandraRepository: PostReverseCassandraRepository,
    private val postSequenceRepository: PostSequenceRepository,
) : PostRepository {

    override suspend fun putMetadata(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        parentId: PostId?,
        postId: PostId,
        metadataType: PostMetadataType,
        value: Any,
    ): Boolean {
        val post = findPost(
            postSpaceKey = PostSpaceKey(
                workspaceId = workspaceId,
                componentId = componentId,
                spaceId = spaceId,
            ),
            postId = postId,
        ) ?: throw PostNotExistsException("포스트($postId)가 존재하지 않습니다")

        if (post.metadata[metadataType] == value) {
            return false
        }

        val key = PostPrimaryKey.of(
            workspaceId = workspaceId,
            componentId = componentId,
            spaceId = spaceId,
            parentId = parentId,
            postNo = postId.postNo,
        )
        postCassandraRepository.putMetadata(key, metadataType, value.toString())
        return true
    }

    override suspend fun create(
        postSpaceKey: PostSpaceKey,
        parentId: PostId?,
        postNo: Long,
        ownerId: String,
        title: String,
        sections: List<PostSectionContentCommand>,
        extra: Map<String, String>,
    ): PostWithSections {
        val post = PostEntity.of(
            workspaceId = postSpaceKey.workspaceId,
            componentId = postSpaceKey.componentId,
            spaceId = postSpaceKey.spaceId,
            ownerId = ownerId,
            parentId = parentId,
            postNo = postNo,
            title = title,
            extra = extra,
        )

        val postSections = postSectionManager.makePostSections(
            requests = sections,
            postSpaceKey = postSpaceKey,
            postNo = postNo,
            parentId = parentId,
            ownerId = ownerId,
        )

        reactiveCassandraOperations.batchOps()
            .upsert(post)
            .upsert(PostReverse.of(post))
            .upsert(postSections)
            .executeCoroutine()

        return post.toPostWithSections(
            sections = postSectionManager.makePostSectionContentResponse(postSections)
        )
    }

    override suspend fun modify(
        postSpaceKey: PostSpaceKey,
        parentId: PostId?,
        postNo: Long,
        ownerId: String,
        title: String?,
        sections: List<PostSectionContentCommand>?,
        extra: Map<String, String>?,
    ): PostPatchResponse {
        val post = postCassandraRepository.findByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotIdAndKeyPostNo(
            workspaceId = postSpaceKey.workspaceId,
            componentId = postSpaceKey.componentId,
            spaceId = postSpaceKey.spaceId,
            parentId = parentId?.parentId ?: StringUtils.EMPTY,
            slotId = PostSlotAssigner.assign(postNo),
            postNo = postNo,
        ) ?: throw PostNotExistsException(message = "해당하는 포스트($postNo)는 존재하지 않습니다 [postSpaceKey: $postSpaceKey]")

        if (!post.isOwner(ownerId)) {
            throw NoPermissionException("계정($ownerId)는 해당하는 포스트($postNo)를 수정할 권한이 없습니다 [postSpaceKey: $postSpaceKey]")
        }

        val previousPostSections = postSectionCassandraRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotIdAndKeyPostNo(
            workspaceId = postSpaceKey.workspaceId,
            componentId = postSpaceKey.componentId,
            spaceId = postSpaceKey.spaceId,
            parentId = parentId?.parentId ?: StringUtils.EMPTY,
            slotId = PostSlotAssigner.assign(postNo),
            postNo = postNo,
        ).toList()

        var hasChanged = post.patch(
            title = title,
            extra = extra,
        )

        if (sections == null) {
            reactiveCassandraOperations.batchOps()
                .upsert(post)
                .upsert(PostReverse.of(post))
                .executeCoroutine()
            return PostPatchResponse(
                post = post.toPostWithSections(
                    sections = postSectionManager.makePostSectionContentResponse(previousPostSections)
                ),
                hasChanged = hasChanged
            )
        }

        val newPostSections = postSectionManager.makePostSections(
            requests = sections,
            postSpaceKey = postSpaceKey,
            postNo = postNo,
            parentId = post.key.parentPostId,
            ownerId = ownerId,
        )

        val newPostSectionPriorities = newPostSections.map { section -> section.key.priority } // 동일한 키로 변경시 삭제되는 버그가 있어서 upsert로 동작하도록 필터링
        val deletedPostSections = previousPostSections.filterNot { section -> newPostSectionPriorities.contains(section.key.priority) } - newPostSections.toSet()
        val insertedPostSections = newPostSections - previousPostSections.toSet()

        reactiveCassandraOperations.batchOps()
            .upsert(post)
            .upsert(PostReverse.of(post))
            .delete(deletedPostSections)
            .upsert(insertedPostSections)
            .executeCoroutine()

        hasChanged = hasChanged || (deletedPostSections.isNotEmpty() && insertedPostSections.isNotEmpty())

        return PostPatchResponse(
            post = post.toPostWithSections(
                sections = postSectionManager.makePostSectionContentResponse(previousPostSections)
            ),
            hasChanged = hasChanged
        )
    }

    override suspend fun delete(postSpaceKey: PostSpaceKey, ownerId: String, postId: PostId) {
        val postReverse =
            postReverseCassandraRepository.findByKeyWorkspaceIdAndKeyComponentIdAndKeyDistributionKeyAndKeyOwnerIdAndKeyPostNoAndKeyParentIdAndKeySpaceId(
                workspaceId = postSpaceKey.workspaceId,
                componentId = postSpaceKey.componentId,
                distributionKey = PostDistributionKey.makeKey(ownerId),
                ownerId = ownerId,
                postNo = postId.postNo,
                parentId = postId.parentId ?: StringUtils.EMPTY,
                spaceId = postSpaceKey.spaceId,
            ) ?: return

        val post = postCassandraRepository.findByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotIdAndKeyPostNo(
            workspaceId = postSpaceKey.workspaceId,
            componentId = postSpaceKey.componentId,
            spaceId = postSpaceKey.spaceId,
            slotId = postReverse.slotId,
            parentId = postId.parentId ?: StringUtils.EMPTY,
            postNo = postId.postNo,
        )

        val postSections = postSectionCassandraRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotIdAndKeyPostNo(
            workspaceId = postSpaceKey.workspaceId,
            componentId = postSpaceKey.componentId,
            spaceId = postSpaceKey.spaceId,
            parentId = postReverse.key.parentId,
            postNo = postId.postNo,
            slotId = PostSlotAssigner.assign(postNo = postId.postNo)
        ).toList()

        reactiveCassandraOperations.batchOps()
            .delete(post)
            .delete(postReverse)
            .delete(postSections)
            .executeCoroutine()
    }

    override suspend fun findPost(
        postSpaceKey: PostSpaceKey,
        postId: PostId,
    ): Post? {
        return postCassandraRepository.findByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotIdAndKeyPostNo(
            workspaceId = postSpaceKey.workspaceId,
            componentId = postSpaceKey.componentId,
            spaceId = postSpaceKey.spaceId,
            parentId = postId.parentId ?: StringUtils.EMPTY,
            slotId = PostSlotAssigner.assign(postNo = postId.postNo),
            postNo = postId.postNo,
        )?.toPost()
    }

    override suspend fun findPostWithSections(postSpaceKey: PostSpaceKey, postId: PostId): PostWithSections? {
        val post = postCassandraRepository.findByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotIdAndKeyPostNo(
            workspaceId = postSpaceKey.workspaceId,
            componentId = postSpaceKey.componentId,
            spaceId = postSpaceKey.spaceId,
            parentId = postId.parentId ?: StringUtils.EMPTY,
            slotId = PostSlotAssigner.assign(postId.postNo),
            postNo = postId.postNo,
        ) ?: return null

        val sections = postSectionCassandraRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotIdAndKeyPostNo(
            workspaceId = postSpaceKey.workspaceId,
            componentId = postSpaceKey.componentId,
            spaceId = postSpaceKey.spaceId,
            parentId = postId.parentId ?: StringUtils.EMPTY,
            slotId = PostSectionSlotAssigner.assign(postId.postNo),
            postNo = postId.postNo,
        )

        return post.toPostWithSections(
            sections = postSectionManager.makePostSectionContentResponse(sections.toList())
        )
    }

    override suspend fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotId(
        postSpaceKey: PostSpaceKey,
        parentId: PostId?,
        cursorRequest: CursorRequest,
        sortBy: PostSortBy,
    ): Slice<PostWithSections, String> = coroutineScope {
        val (slot: Long, posts: List<PostEntity>) = when (sortBy to cursorRequest.direction) {
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
                .groupBy { post -> PostSlotAssigner.assign(postNo = post.key.postNo) }
                .flatMap { (slotId, posts) ->
                    postSectionCassandraRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotIdAndKeyPostNoIn(
                        workspaceId = postSpaceKey.workspaceId,
                        componentId = postSpaceKey.componentId,
                        spaceId = postSpaceKey.spaceId,
                        parentId = parentId?.serialize() ?: StringUtils.EMPTY,
                        slotId = slotId,
                        postNos = posts.map { post -> post.key.postNo },
                    ).toList()
                }.groupBy { postSection -> postSection.key.postNo }

            return@coroutineScope Slice(
                data = posts.subList(0, cursorRequest.pageSize.coerceAtMost(posts.size))
                    .map { post ->
                        post.toPostWithSections(
                            sections = postSectionManager.makePostSectionContentResponse(
                                postSections[post.key.postNo] ?: emptyList()
                            )
                        )
                    },
                cursor = CursorUtils.getCursor(
                    listWithNextCursor = posts,
                    pageSize = cursorRequest.pageSize,
                    keyGenerator = { post -> post?.key?.postNo?.toString() }
                )
            )
        }

        val morePosts = when (cursorRequest.direction) {
            CursorDirection.NEXT -> {
                postCassandraRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotId(
                    workspaceId = postSpaceKey.workspaceId,
                    componentId = postSpaceKey.componentId,
                    spaceId = postSpaceKey.spaceId,
                    parentId = parentId?.serialize() ?: StringUtils.EMPTY,
                    slotId = slot - 1,
                    pageable = CassandraPageRequest.first(cursorRequest.pageSize - posts.size + 1),
                ).toList()
            }

            CursorDirection.PREVIOUS -> {
                postCassandraRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotIdOrderByKeyPostNoAsc(
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
            .groupBy { PostSectionPartitionKey.from(it) }
            .map { (sectionPartition, posts) ->
                async {
                    postSectionCassandraRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotIdAndKeyPostNoIn(
                        workspaceId = sectionPartition.workspaceId,
                        componentId = sectionPartition.componentId,
                        spaceId = sectionPartition.spaceId,
                        parentId = sectionPartition.parentId,
                        slotId = sectionPartition.slotId,
                        postNos = posts.map { post -> post.key.postNo },
                    ).toList()
                }
            }.awaitAll().flatten().groupBy { postSection -> postSection.key.postNo }

        return@coroutineScope Slice(
            data = data.map { post ->
                post.toPostWithSections(
                    sections = postSectionManager.makePostSectionContentResponse(
                        postSections[post.key.postNo] ?: emptyList()
                    )
                )
            },
            cursor = CursorUtils.getCursor(
                listWithNextCursor = morePosts,
                pageSize = cursorRequest.pageSize - posts.size,
                keyGenerator = { post -> post?.key?.postNo?.toString() }
            )
        )
    }

    private suspend fun listNextPosts(
        cursorRequest: CursorRequest,
        parentId: PostId?,
        postSpaceKey: PostSpaceKey,
    ): Pair<Long, List<PostEntity>> {
        val cursor = cursorRequest.cursor
        if (cursor == null) {
            val lastSlotId = PostSlotAssigner.assign(
                postNo = postSequenceRepository.getLastSequence(
                    postSpaceKey = postSpaceKey,
                    parentId = parentId
                )
            )
            return lastSlotId to postCassandraRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotId(
                workspaceId = postSpaceKey.workspaceId,
                componentId = postSpaceKey.componentId,
                spaceId = postSpaceKey.spaceId,
                parentId = parentId?.serialize() ?: StringUtils.EMPTY,
                slotId = lastSlotId,
                pageable = CassandraPageRequest.first(cursorRequest.pageSize + 1),
            ).toList()
        }

        val currentSlot = PostSlotAssigner.assign(
            postNo = cursor.toLongOrNull()
                ?: throw InvalidCursorException("잘못된 CursorResponse(${cursorRequest.cursor})입니다")
        )
        return currentSlot to postCassandraRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotIdAndKeyPostNoLessThan(
            workspaceId = postSpaceKey.workspaceId,
            componentId = postSpaceKey.componentId,
            spaceId = postSpaceKey.spaceId,
            parentId = parentId?.serialize() ?: StringUtils.EMPTY,
            slotId = currentSlot,
            pageable = CassandraPageRequest.first(cursorRequest.pageSize + 1),
            postNo = cursor.toLongOrNull()
                ?: throw InvalidCursorException("잘못된 CursorResponse(${cursorRequest.cursor})입니다"),
        ).toList()
    }

    private suspend fun listPreviousPosts(
        cursorRequest: CursorRequest,
        parentId: PostId?,
        postSpaceKey: PostSpaceKey,
    ): Pair<Long, List<PostEntity>> {
        val cursor = cursorRequest.cursor
        if (cursor == null) {
            val firstSlotId = PostSlotAssigner.FIRST_SLOT_ID
            return firstSlotId to postCassandraRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotIdOrderByKeyPostNoAsc(
                workspaceId = postSpaceKey.workspaceId,
                componentId = postSpaceKey.componentId,
                spaceId = postSpaceKey.spaceId,
                parentId = parentId?.serialize() ?: StringUtils.EMPTY,
                slotId = firstSlotId,
                pageable = CassandraPageRequest.first(cursorRequest.pageSize + 1),
            ).toList()
        }
        val currentSlot = PostSlotAssigner.assign(
            postNo = cursor.toLongOrNull()
                ?: throw InvalidCursorException("잘못된 CursorResponse(${cursorRequest.cursor})입니다"),
        )
        return currentSlot to postCassandraRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotIdAndKeyPostNoGreaterThanOrderByKeyPostNoAsc(
            workspaceId = postSpaceKey.workspaceId,
            componentId = postSpaceKey.componentId,
            spaceId = postSpaceKey.spaceId,
            parentId = parentId?.serialize() ?: StringUtils.EMPTY,
            slotId = currentSlot,
            postNo = cursor.toLongOrNull()
                ?: throw InvalidCursorException("잘못된 CursorResponse(${cursorRequest.cursor})입니다"),
            pageable = CassandraPageRequest.first(cursorRequest.pageSize + 1),
        ).toList()
    }

    override suspend fun listOwnerPosts(
        workspaceId: String,
        componentId: String,
        ownerId: String,
        cursorRequest: CursorRequest,
    ): Slice<PostWithSections, String> = coroutineScope {
        val posts = postReverses(
            workspaceId = workspaceId,
            componentId = componentId,
            ownerId = ownerId,
            cursorRequest = cursorRequest,
        )

        val postSections = getPostReverseSections(
            posts.subList(0, cursorRequest.pageSize.coerceAtMost(posts.size))
        )

        return@coroutineScope Slice(
            data = posts.subList(0, cursorRequest.pageSize.coerceAtMost(posts.size))
                .map { post ->
                    post.toPostWithSEctions(
                        sections = postSectionManager.makePostSectionContentResponse(
                            postSections[post.key.postNo] ?: emptyList()
                        )
                    )
                },
            cursor = CursorUtils.getCursor(
                listWithNextCursor = posts,
                pageSize = cursorRequest.pageSize,
                keyGenerator = { post -> post?.key?.postNo?.toString() }
            )
        )
    }

    private suspend fun postReverses(
        workspaceId: String,
        componentId: String,
        ownerId: String,
        cursorRequest: CursorRequest,
    ): List<PostReverse> {
        val cursor = cursorRequest.cursor
        if (cursor.isNullOrBlank()) {
            return postReverseCassandraRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyDistributionKeyAndKeyOwnerId(
                workspaceId = workspaceId,
                componentId = componentId,
                distributionKey = PostDistributionKey.makeKey(ownerId),
                ownerId = ownerId,
                pageable = CassandraPageRequest.first(cursorRequest.pageSize + 1)
            ).toList()
        }
        return postReverseCassandraRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyDistributionKeyAndKeyOwnerIdAndKeyPostNoLessThan(
            workspaceId = workspaceId,
            componentId = componentId,
            distributionKey = PostDistributionKey.makeKey(ownerId),
            ownerId = ownerId,
            postNo = cursor.toLong(),
            pageable = CassandraPageRequest.first(cursorRequest.pageSize + 1)
        ).toList()
    }

    private suspend fun getPostReverseSections(
        posts: List<PostReverse>,
    ): Map<Long, List<PostSectionEntity>> = coroutineScope {
        return@coroutineScope posts.groupBy { post -> PostSectionPartitionKey.from(post) }
            .map { (sectionPartition, posts) ->
                async {
                    postSectionCassandraRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotIdAndKeyPostNoIn(
                        workspaceId = sectionPartition.workspaceId,
                        componentId = sectionPartition.componentId,
                        spaceId = sectionPartition.spaceId,
                        parentId = sectionPartition.parentId,
                        slotId = sectionPartition.slotId,
                        postNos = posts.map { post -> post.key.postNo },
                    ).toList()
                }
            }.awaitAll().flatten().groupBy { postSection -> postSection.key.postNo }
    }

    override suspend fun clear(workspaceId: String, componentId: String, distributionKey: DistributionKey): Long {
        var pageable: Pageable = CassandraPageRequest.first(500)
        var deletedCount = 0L
        do {
            val postReverses = postReverseCassandraRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyDistributionKey(
                workspaceId = workspaceId,
                componentId = componentId,
                distributionKey = distributionKey.key,
                pageable = pageable,
            )

            postReverses.content.groupBy { postReverse -> PostPartitionKey.from(postReverse) }.keys
                .forEach { key ->
                    postCassandraRepository.deleteAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotId(
                        workspaceId = key.workspaceId,
                        componentId = key.componentId,
                        spaceId = key.spaceId,
                        parentId = key.parentId,
                        slotId = key.slotId,
                    )
                }

            postReverseCassandraRepository.deleteAllByKeyWorkspaceIdAndKeyComponentIdAndKeyDistributionKey(
                workspaceId = workspaceId,
                componentId = componentId,
                distributionKey = distributionKey.key,
            )

            deletedCount += postReverses.size

            pageable = postReverses.nextPageable()
        } while (postReverses.hasNext())

        return deletedCount
    }

}
