package com.story.platform.core.domain.component

import com.story.platform.core.common.error.ConflictException
import com.story.platform.core.common.error.NotFoundException
import org.springframework.stereotype.Service

@Service
class ComponentManager(
    private val componentRepository: ComponentRepository,
) {

    suspend fun create(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        description: String,
    ) {
        val exists = componentRepository.existsById(
            ComponentPrimaryKey(
                workspaceId = workspaceId,
                resourceId = resourceId,
                componentId = componentId,
            )
        )

        if (exists) {
            throw ConflictException("이미 등록된 워크스페이스($workspaceId)의 컴포넌트($resourceId-$componentId)입니다.")
        }

        val component = Component.of(
            workspaceId = workspaceId,
            resourceId = resourceId,
            componentId = componentId,
            description = description,
            status = ComponentStatus.ENABLED
        )
        componentRepository.save(component)
    }

    suspend fun patch(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        description: String?,
        status: ComponentStatus?,
    ) {
        val workspaceComponent = componentRepository.findById(
            ComponentPrimaryKey(
                workspaceId = workspaceId,
                resourceId = resourceId,
                componentId = componentId,
            )
        ) ?: throw NotFoundException("워크스페이스($workspaceId)에 등록되지 않은 컴포넌트($resourceId-$componentId)입니다")

        workspaceComponent.patch(description = description, status = status)

        componentRepository.save(workspaceComponent)
    }

}
