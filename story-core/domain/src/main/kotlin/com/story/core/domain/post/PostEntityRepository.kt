package com.story.core.domain.post

import com.story.core.common.error.NoPermissionException
import com.story.core.domain.post.section.PostSectionCassandraRepository
import com.story.core.domain.post.section.PostSectionContentCommand
import com.story.core.domain.post.section.PostSectionManager
import com.story.core.domain.post.section.PostSectionSlotAssigner
import com.story.core.support.cassandra.executeCoroutine
import com.story.core.support.cassandra.upsert
import kotlinx.coroutines.flow.toList
import org.apache.commons.lang3.StringUtils
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.stereotype.Repository

@Repository
class PostEntityRepository(
    private val postCassandraRepository: PostCassandraRepository,
    private val postSectionCassandraRepository: PostSectionCassandraRepository,
    private val postSectionManager: PostSectionManager,
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
    private val postReverseCassandraRepository: PostReverseCassandraRepository,
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

        return PostWithSections.of(
            post = post,
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
                post = PostWithSections.of(
                    post = post,
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
            post = PostWithSections.of(
                post = post,
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

        return PostWithSections.of(
            post = post,
            sections = postSectionManager.makePostSectionContentResponse(sections.toList())
        )
    }

}
