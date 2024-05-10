package com.story.core.domain.post

import com.story.core.common.error.NoPermissionException
import com.story.core.domain.post.section.PostSectionContentRequest
import com.story.core.domain.post.section.PostSectionManager
import com.story.core.domain.post.section.PostSectionRepository
import com.story.core.domain.post.section.PostSectionSlotAssigner
import com.story.core.infrastructure.cache.CacheEvict
import com.story.core.infrastructure.cache.CacheStrategy
import com.story.core.infrastructure.cache.CacheType
import com.story.core.infrastructure.cassandra.executeCoroutine
import com.story.core.infrastructure.cassandra.upsert
import com.story.core.infrastructure.lock.DistributedLock
import com.story.core.infrastructure.lock.DistributedLockType
import kotlinx.coroutines.flow.toList
import org.apache.commons.lang3.StringUtils
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.stereotype.Service

@Service
class PostModifier(
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
    private val postRepository: PostRepository,
    private val postSectionRepository: PostSectionRepository,
    private val postSectionManager: PostSectionManager,
) {

    @DistributedLock(
        lockType = DistributedLockType.POST,
        key = "'workspaceId:' + {#postSpaceKey.workspaceId} + ':componentId:' + {#postSpaceKey.componentId} + ':spaceId:' + {#postSpaceKey.spaceId} + ':parentId:' + {#postId.parentId} + ':postNo:' + {#postId.postNo}",
    )
    @CacheEvict(
        cacheType = CacheType.POST,
        key = "'workspaceId:' + {#postSpaceKey.workspaceId} + ':componentId:' + {#postSpaceKey.componentId} + ':spaceId:' + {#postSpaceKey.spaceId} + ':parentId:' + {#postId.parentId} + ':postNo:' + {#postId.postNo}",
        targetCacheStrategies = [CacheStrategy.GLOBAL]
    )
    suspend fun patchPost(
        postSpaceKey: PostSpaceKey,
        ownerId: String,
        postId: PostId,
        title: String?,
        sections: List<PostSectionContentRequest>?,
        extra: Map<String, String>?,
    ): PostPatchResponse {
        val slotId = PostSlotAssigner.assign(postId.postNo)

        val post = postRepository.findByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotIdAndKeyPostNo(
            workspaceId = postSpaceKey.workspaceId,
            componentId = postSpaceKey.componentId,
            spaceId = postSpaceKey.spaceId,
            parentId = postId.parentId ?: StringUtils.EMPTY,
            slotId = slotId,
            postNo = postId.postNo,
        ) ?: throw PostNotExistsException(message = "해당하는 포스트($postId)는 존재하지 않습니다 [postSpaceKey: $postSpaceKey]")

        if (!post.isOwner(ownerId)) {
            throw NoPermissionException("계정($ownerId)는 해당하는 포스트($postId)를 수정할 권한이 없습니다 [postSpaceKey: $postSpaceKey]")
        }

        val previousPostSections = postSectionRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotIdAndKeyPostNo(
            workspaceId = postSpaceKey.workspaceId,
            componentId = postSpaceKey.componentId,
            spaceId = postSpaceKey.spaceId,
            parentId = postId.parentId ?: StringUtils.EMPTY,
            slotId = PostSectionSlotAssigner.assign(postId = postId.postNo),
            postNo = postId.postNo,
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
                post = Post.of(
                    post = post,
                    sections = postSectionManager.makePostSectionContentResponse(previousPostSections)
                ),
                hasChanged = hasChanged
            )
        }

        val newPostSections = postSectionManager.makePostSections(
            requests = sections,
            postSpaceKey = postSpaceKey,
            postNo = postId.postNo,
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
            post = Post.of(
                post = post,
                sections = postSectionManager.makePostSectionContentResponse(previousPostSections)
            ),
            hasChanged = hasChanged
        )
    }

    @DistributedLock(
        lockType = DistributedLockType.POST,
        key = "'workspaceId:' + {#postSpaceKey.workspaceId} + ':componentId:' + {#postSpaceKey.componentId} + ':spaceId:' + {#postSpaceKey.spaceId} + ':parentId:' + {#postId.parentId} + ':postNo:' + {#postId.postNo}",
    )
    @CacheEvict(
        cacheType = CacheType.POST,
        key = "'workspaceId:' + {#postSpaceKey.workspaceId} + ':componentId:' + {#postSpaceKey.componentId} + ':spaceId:' + {#postSpaceKey.spaceId} + ':parentId:' + {#postId.parentId} + ':postNo:' + {#postId.postNo}",
        targetCacheStrategies = [CacheStrategy.GLOBAL]
    )
    suspend fun putMetadata(
        postSpaceKey: PostSpaceKey,
        postId: PostId,
        metadataType: PostMetadataType,
        value: Any,
    ): Boolean {
        val post = postRepository.findByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotIdAndKeyPostNo(
            workspaceId = postSpaceKey.workspaceId,
            componentId = postSpaceKey.componentId,
            spaceId = postSpaceKey.spaceId,
            parentId = postId.parentId ?: StringUtils.EMPTY,
            slotId = PostSlotAssigner.assign(postNo = postId.postNo),
            postNo = postId.postNo,
        ) ?: throw PostNotExistsException("포스트($postId)가 존재하지 않습니다 [postSpaceKey: $postSpaceKey]")

        if (post.metadata[metadataType] == value) {
            return false
        }

        postRepository.putMetadata(post.key, metadataType, value.toString())
        return true
    }

}
