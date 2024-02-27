package com.story.core.domain.component

import com.story.core.common.logger.LoggerExtension.log
import com.story.core.domain.resource.ResourceId
import com.story.core.infrastructure.cache.CacheEvict
import com.story.core.infrastructure.cache.CacheStrategy
import com.story.core.infrastructure.cache.CacheType
import org.springframework.stereotype.Service

@Service
class ComponentLocalCacheEvictManager {

    @CacheEvict(
        cacheType = CacheType.COMPONENT,
        key = "'workspaceId:' + {#workspaceId} + ':resourceId:' + {#resourceId} + ':componentId:' + {#componentId}",
        targetCacheStrategies = [CacheStrategy.LOCAL],
    )
    suspend fun evictComponent(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
    ) {
        log.debug { "Component 캐시가 만료됩니다 [workspaceId: $workspaceId resourceId: $resourceId componentId: $componentId]" }
    }

}
