package com.story.platform.core.domain.component

import com.story.platform.core.common.logger.LoggerExtension.log
import com.story.platform.core.domain.resource.ResourceId
import com.story.platform.core.support.cache.CacheEvict
import com.story.platform.core.support.cache.CacheStrategyType
import com.story.platform.core.support.cache.CacheType
import org.springframework.stereotype.Service

@Service
class ComponentLocalCacheEvictionManager {

    @CacheEvict(
        cacheType = CacheType.COMPONENT,
        key = "'workspaceId:' + {#workspaceId} + ':resourceId:' + {#resourceId} + ':componentId:' + {#componentId}",
        targetCacheStrategies = [CacheStrategyType.LOCAL],
    )
    suspend fun evictComponent(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
    ) {
        log.info { "Component 캐시가 만료됩니다 [workspaceId: $workspaceId resourceId: $resourceId componentId: $componentId]" }
    }

}
