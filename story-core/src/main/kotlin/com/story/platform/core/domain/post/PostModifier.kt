package com.story.platform.core.domain.post

import com.story.platform.core.common.error.NoPermissionException
import com.story.platform.core.infrastructure.cache.CacheEvict
import com.story.platform.core.infrastructure.cache.CacheStrategy
import com.story.platform.core.infrastructure.cache.CacheType
import com.story.platform.core.infrastructure.cassandra.executeCoroutine
import com.story.platform.core.infrastructure.cassandra.upsert
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.stereotype.Service

@Service
class PostModifier(
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
    private val postRepository: PostRepository,
) {

    @CacheEvict(
        cacheType = CacheType.POST,
        key = "'workspaceId:' + {#postSpaceKey.workspaceId} + ':componentId:' + {#postSpaceKey.componentId} + ':spaceId:' + {#postSpaceKey.spaceId} + ':postId:' + {#postId}",
        targetCacheStrategies = [CacheStrategy.GLOBAL]
    )
    suspend fun patch(
        postSpaceKey: PostSpaceKey,
        accountId: String,
        postId: Long,
        title: String?,
        content: String?,
        extra: Map<String, String?>?,
    ): PostPatchResponse {
        val slotId = PostSlotAssigner.assign(postId)

        val post = postRepository.findByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeySlotIdAndKeyPostId(
            workspaceId = postSpaceKey.workspaceId,
            componentId = postSpaceKey.componentId,
            spaceId = postSpaceKey.spaceId,
            slotId = slotId,
            postId = postId,
        ) ?: throw PostNotExistsException(message = "해당하는 포스트($postId)는 존재하지 않습니다 [postSpaceKey: $postSpaceKey]")

        if (!post.isOwner(accountId)) {
            throw NoPermissionException("계정($accountId)는 해당하는 포스트($postId)를 수정할 권한이 없습니다 [postSpaceKey: $postSpaceKey]")
        }

        val hasChanged = post.patch(
            title = title,
            content = content,
            extra = extra,
        )

        reactiveCassandraOperations.batchOps()
            .upsert(post)
            .upsert(PostReverse.of(post))
            .executeCoroutine()

        return PostPatchResponse(post = PostResponse.of(post), hasChanged = hasChanged)
    }

}
