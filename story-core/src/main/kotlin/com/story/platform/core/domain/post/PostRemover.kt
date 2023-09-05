package com.story.platform.core.domain.post

import com.story.platform.core.common.distribution.XLargeDistributionKey
import com.story.platform.core.infrastructure.cassandra.executeCoroutine
import com.story.platform.core.support.cache.CacheEvict
import com.story.platform.core.support.cache.CacheStrategy
import com.story.platform.core.support.cache.CacheType
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
    suspend fun remove(
        postSpaceKey: PostSpaceKey,
        accountId: String,
        postId: Long,
    ) {
        val postReverse =
            postReverseRepository.findByKeyWorkspaceIdAndKeyComponentIdAndKeyDistributionKeyAndKeyAccountIdAndKeyPostIdAndKeySpaceId(
                workspaceId = postSpaceKey.workspaceId,
                componentId = postSpaceKey.componentId,
                distributionKey = XLargeDistributionKey.makeKey(accountId).key,
                accountId = accountId,
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
