package com.story.core.domain.component

import com.story.core.domain.resource.ResourceId
import com.story.core.support.cache.CacheEvict
import com.story.core.support.cache.CacheType
import org.springframework.stereotype.Service

@Service
class ComponentModifier(
    private val componentWriteRepository: ComponentWriteRepository,
) {

    @CacheEvict(
        cacheType = CacheType.COMPONENT,
        key = "'workspaceId:' + {#workspaceId} + ':resourceId:' + {#resourceId} + ':componentId:' + {#componentId}",
    )
    suspend fun patchComponent(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        description: String?,
        status: ComponentStatus?,
    ): Component {
        return componentWriteRepository.update(
            workspaceId = workspaceId,
            resourceId = resourceId,
            componentId = componentId,
            description = description,
            status = status,
        )
    }

}
