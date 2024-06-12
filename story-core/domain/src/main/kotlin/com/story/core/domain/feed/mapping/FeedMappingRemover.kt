package com.story.core.domain.feed.mapping

import com.story.core.support.cache.CacheEvict
import com.story.core.support.cache.CacheStrategy
import com.story.core.support.cache.CacheType
import com.story.core.support.lock.DistributedLock
import com.story.core.support.lock.DistributedLockType
import org.springframework.stereotype.Service

@Service
class FeedMappingRemover(
    private val feedMappingWriteRepository: FeedMappingWriteRepository,
) {

    @CacheEvict(
        cacheType = CacheType.FEED_MAPPING,
        key = "'workspaceId:' + {#command.workspaceId} + ':sourceResourceId:' + {#command.sourceResourceId} + ':sourceComponentId:' + {#command.sourceComponentId}",
        targetCacheStrategies = [CacheStrategy.GLOBAL]
    )
    @DistributedLock(
        lockType = DistributedLockType.FEED_MAPPING,
        key = "'workspaceId:' + {#command.workspaceId} + ':feedComponentId:' + {#command.feedComponentId} + ':sourceResourceId:' + {#command.sourceResourceId} + ':sourceComponentId:' + {#command.sourceComponentId}"
    )
    suspend fun remove(
        command: FeedMappingRemoveCommand,
    ) {
        feedMappingWriteRepository.delete(
            workspaceId = command.workspaceId,
            feedComponentId = command.feedComponentId,
            sourceResourceId = command.sourceResourceId,
            sourceComponentId = command.sourceComponentId,
            subscriptionComponentId = command.subscriptionComponentId,
        )
    }

}
