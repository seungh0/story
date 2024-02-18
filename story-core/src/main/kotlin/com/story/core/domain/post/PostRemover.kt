package com.story.core.domain.post

import com.story.core.infrastructure.cache.CacheEvict
import com.story.core.infrastructure.cache.CacheStrategy
import com.story.core.infrastructure.cache.CacheType
import com.story.core.infrastructure.cassandra.executeCoroutine
import org.apache.commons.lang3.StringUtils
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.stereotype.Service

@Service
class PostRemover(
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
    private val postRepository: PostRepository,
    private val postReverseRepository: PostReverseRepository,
) {

    @CacheEvict(
        cacheType = CacheType.POST,
        key = "'workspaceId:' + {#postSpaceKey.workspaceId} + ':componentId:' + {#postSpaceKey.componentId} + ':spaceId:' + {#postSpaceKey.spaceId} + ':parentId:' + {#postId.parentId} + ':postId:' + {#postId.postId}",
        targetCacheStrategies = [CacheStrategy.GLOBAL]
    )
    suspend fun removePost(
        postSpaceKey: PostSpaceKey,
        ownerId: String,
        postId: PostKey,
    ) {
        val postReverse =
            postReverseRepository.findByKeyWorkspaceIdAndKeyComponentIdAndKeyDistributionKeyAndKeyOwnerIdAndKeyParentIdAndKeyPostIdAndKeySpaceId(
                workspaceId = postSpaceKey.workspaceId,
                componentId = postSpaceKey.componentId,
                distributionKey = PostDistributionKey.makeKey(ownerId),
                ownerId = ownerId,
                parentId = postId.parentId ?: StringUtils.EMPTY,
                postId = postId.postId,
                spaceId = postSpaceKey.spaceId,
            ) ?: return

        val post = postRepository.findByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotIdAndKeyPostId(
            workspaceId = postSpaceKey.workspaceId,
            componentId = postSpaceKey.componentId,
            spaceId = postSpaceKey.spaceId,
            slotId = postReverse.slotId,
            parentId = postId.parentId ?: StringUtils.EMPTY,
            postId = postId.postId,
        )

        reactiveCassandraOperations.batchOps()
            .delete(post)
            .delete(postReverse)
            .executeCoroutine()
    }

}
