package com.story.core.domain.component

import com.story.core.domain.resource.ResourceId
import com.story.core.infrastructure.cache.CacheType
import com.story.core.infrastructure.cache.Cacheable
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class ComponentReaderWithCache(
    private val componentReader: ComponentReader,
) {

    @Cacheable(
        cacheType = CacheType.COMPONENT,
        key = "'workspaceId:' + {#workspaceId} + ':resourceId:' + {#resourceId} + ':componentId:' + {#componentId}",
    )
    suspend fun getComponent(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
    ): Optional<Component> {
        val component = componentReader.getComponent(
            workspaceId = workspaceId,
            resourceId = resourceId,
            componentId = componentId,
        ) ?: return Optional.empty()
        return Optional.of(component)
    }

}
