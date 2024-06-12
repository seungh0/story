package com.story.core.domain.component

import com.story.core.domain.resource.ResourceId
import com.story.core.support.cache.CacheEvict
import com.story.core.support.cache.CacheStrategy
import com.story.core.support.cache.CacheType
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
    }

}
