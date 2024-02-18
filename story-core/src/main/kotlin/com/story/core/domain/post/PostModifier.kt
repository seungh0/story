package com.story.core.domain.post

import com.story.core.common.error.NoPermissionException
import com.story.core.domain.post.section.PostSection
import com.story.core.domain.post.section.PostSectionContentRequest
import com.story.core.domain.post.section.PostSectionRepository
import com.story.core.domain.post.section.PostSectionSlotAssigner
import com.story.core.infrastructure.cache.CacheEvict
import com.story.core.infrastructure.cache.CacheStrategy
import com.story.core.infrastructure.cache.CacheType
import com.story.core.infrastructure.cassandra.executeCoroutine
import com.story.core.infrastructure.cassandra.upsert
import kotlinx.coroutines.flow.toList
import org.apache.commons.lang3.StringUtils
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.stereotype.Service

@Service
class PostModifier(
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
    private val postRepository: PostRepository,
    private val postSectionRepository: PostSectionRepository,
) {

    @CacheEvict(
        cacheType = CacheType.POST,
        key = "'workspaceId:' + {#postSpaceKey.workspaceId} + ':componentId:' + {#postSpaceKey.componentId} + ':spaceId:' + {#postSpaceKey.spaceId} + ':parentId:' + {#postId.parentId} + ':postId:' + {#postId.postId}",
        targetCacheStrategies = [CacheStrategy.GLOBAL]
    )
    suspend fun patchPost(
        postSpaceKey: PostSpaceKey,
        ownerId: String,
        postId: PostKey,
        title: String?,
        sections: List<PostSectionContentRequest>?,
    ): PostPatchResponse {
        val slotId = PostSlotAssigner.assign(postId.postId)

        val post = postRepository.findByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotIdAndKeyPostId(
            workspaceId = postSpaceKey.workspaceId,
            componentId = postSpaceKey.componentId,
            spaceId = postSpaceKey.spaceId,
            parentId = postId.parentId ?: StringUtils.EMPTY,
            slotId = slotId,
            postId = postId.postId,
        ) ?: throw PostNotExistsException(message = "해당하는 포스트($postId)는 존재하지 않습니다 [postSpaceKey: $postSpaceKey]")

        if (!post.isOwner(ownerId)) {
            throw NoPermissionException("계정($ownerId)는 해당하는 포스트($postId)를 수정할 권한이 없습니다 [postSpaceKey: $postSpaceKey]")
        }

        val previousPostSections = postSectionRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotIdAndKeyPostId(
            workspaceId = postSpaceKey.workspaceId,
            componentId = postSpaceKey.componentId,
            spaceId = postSpaceKey.spaceId,
            parentId = postId.parentId ?: StringUtils.EMPTY,
            slotId = PostSectionSlotAssigner.assign(postId = postId.postId),
            postId = postId.postId,
        ).toList()

        var hasChanged = post.patch(
            title = title,
        )

        if (sections == null) {
            reactiveCassandraOperations.batchOps()
                .upsert(post)
                .upsert(PostReverse.of(post))
                .executeCoroutine()
            return PostPatchResponse(
                post = PostResponse.of(post = post, sections = previousPostSections),
                hasChanged = hasChanged
            )
        }

        val newPostSections = sections.map { section ->
            PostSection.of(
                postSpaceKey = postSpaceKey,
                parentId = post.key.parentIdKey,
                postId = postId.postId,
                content = section.toSection(),
                sectionType = section.sectionType(),
                priority = section.priority,
            )
        }

        val deletedPostSections = previousPostSections - newPostSections.toSet()
        val insertedPostSections = newPostSections - previousPostSections.toSet()

        reactiveCassandraOperations.batchOps()
            .upsert(post)
            .upsert(PostReverse.of(post))
            .delete(deletedPostSections)
            .upsert(insertedPostSections) // TODO: 동일한 키로 변경시 삭제되는 버그 있음
            .executeCoroutine()

        hasChanged = hasChanged || (deletedPostSections.isNotEmpty() && insertedPostSections.isNotEmpty())

        return PostPatchResponse(
            post = PostResponse.of(post = post, sections = previousPostSections),
            hasChanged = hasChanged
        )
    }

}
