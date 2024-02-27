package com.story.core.domain.feed.mapping

import com.story.core.common.logger.LoggerExtension.log
import com.story.core.domain.resource.ResourceId
import com.story.core.infrastructure.cache.CacheEvict
import com.story.core.infrastructure.cache.CacheStrategy
import com.story.core.infrastructure.cache.CacheType
import org.springframework.stereotype.Service

@Service
class FeedMappingLocalCacheEvictManager {

    @CacheEvict(
        cacheType = CacheType.FEED_MAPPING,
        key = "'workspaceId:' + {#workspaceId} + ':sourceResourceId:' + {#sourceResourceId} + ':sourceComponentId:' + {#sourceComponentId}",
        targetCacheStrategies = [CacheStrategy.GLOBAL]
    )
    suspend fun evictFeedMapping(
        workspaceId: String,
        sourceResourceId: ResourceId,
        sourceComponentId: String,
    ) {
        log.debug { "FeedMapping 캐시가 만료됩니다 [workspaceId: $workspaceId sourceResourceId: $sourceResourceId sourceComponentId: $sourceComponentId]" }
    }

}
