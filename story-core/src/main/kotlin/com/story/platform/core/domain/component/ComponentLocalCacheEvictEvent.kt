package com.story.platform.core.domain.component

import com.story.platform.core.domain.resource.ResourceId
import com.story.platform.core.support.cache.CacheEvictEventRecord
import com.story.platform.core.support.cache.CacheType

data class ComponentLocalCacheEvictEvent(
    val workspaceId: String,
    val resourceId: ResourceId,
    val componentId: String,
) {

    companion object {
        fun of(workspaceId: String, resourceId: ResourceId, componentId: String) = CacheEvictEventRecord(
            cacheType = CacheType.COMPONENT,
            payload = ComponentLocalCacheEvictEvent(
                workspaceId = workspaceId,
                resourceId = resourceId,
                componentId = componentId,
            )
        )
    }

}
