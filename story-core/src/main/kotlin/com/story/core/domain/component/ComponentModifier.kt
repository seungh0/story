package com.story.core.domain.component

import com.story.core.domain.resource.ResourceId
import com.story.core.infrastructure.cache.CacheEvict
import com.story.core.infrastructure.cache.CacheStrategy
import com.story.core.infrastructure.cache.CacheType
import org.springframework.stereotype.Service

@Service
class ComponentModifier(
    private val componentRepository: ComponentRepository,
) {

    @CacheEvict(
        cacheType = CacheType.COMPONENT,
        key = "'workspaceId:' + {#workspaceId} + ':resourceId:' + {#resourceId} + ':componentId:' + {#componentId}",
        targetCacheStrategies = [CacheStrategy.GLOBAL],
    )
    suspend fun patchComponent(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        description: String?,
        status: ComponentStatus?,
    ): Component {
        val component = componentRepository.findById(
            ComponentPrimaryKey(
                workspaceId = workspaceId,
                resourceId = resourceId,
                componentId = componentId,
            )
        )
            ?: throw ComponentNotExistsException(message = "워크스페이스($workspaceId)에 등록되지 않은 리소스($resourceId) 컴포넌트($componentId)입니다.")

        component.patch(description = description, status = status)

        componentRepository.save(component)

        return Component.of(component)
    }

}
