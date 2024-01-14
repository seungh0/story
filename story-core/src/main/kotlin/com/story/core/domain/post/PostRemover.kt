package com.story.core.domain.post

import com.story.core.infrastructure.cache.CacheEvict
import com.story.core.infrastructure.cache.CacheStrategy
import com.story.core.infrastructure.cache.CacheType
import com.story.core.infrastructure.cassandra.executeCoroutine
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
        key = "'workspaceId:' + {#postSpaceKey.workspaceId} + ':componentId:' + {#postSpaceKey.componentId} + ':spaceId:' + {#postSpaceKey.spaceId} + ':postId:' + {#postId}",
        targetCacheStrategies = [CacheStrategy.GLOBAL]
    )
    suspend fun removePost(
        postSpaceKey: PostSpaceKey,
        ownerId: String,
        postId: Long,
    ) {
        val postReverse =
            postReverseRepository.findByKeyWorkspaceIdAndKeyComponentIdAndKeyDistributionKeyAndKeyOwnerIdAndKeyPostIdAndKeySpaceId(
                workspaceId = postSpaceKey.workspaceId,
                componentId = postSpaceKey.componentId,
                distributionKey = PostDistributionKey.makeKey(ownerId),
                ownerId = ownerId,
                postId = postId,
                spaceId = postSpaceKey.spaceId,
            ) ?: return

        val post = postRepository.findByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeySlotIdAndKeyPostId(
            workspaceId = postSpaceKey.workspaceId,
            componentId = postSpaceKey.componentId,
            spaceId = postSpaceKey.spaceId,
            slotId = postReverse.slotId,
            postId = postId,
        )

        reactiveCassandraOperations.batchOps()
            .delete(post)
            .delete(postReverse)
            .executeCoroutine()
    }

}
