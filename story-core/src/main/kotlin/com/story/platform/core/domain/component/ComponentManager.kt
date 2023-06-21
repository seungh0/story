package com.story.platform.core.domain.component

import com.story.platform.core.common.error.ConflictException
import com.story.platform.core.common.error.ErrorCode
import com.story.platform.core.common.error.NotFoundException
import com.story.platform.core.domain.resource.ResourceId
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
            throw ConflictException(
                message = "워크스페이스($workspaceId)에 이미 등록된 리소스($resourceId) 컴포넌트($componentId)입니다.",
                errorCode = ErrorCode.E404_NOT_FOUND_COMPONENT,
            )
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
        ) ?: throw NotFoundException(
            message = "워크스페이스($workspaceId)에 등록되지 않은 리소스($resourceId) 컴포넌트($componentId)입니다.",
            errorCode = ErrorCode.E404_NOT_FOUND_COMPONENT,
        )

        workspaceComponent.patch(description = description, status = status)

        componentRepository.save(workspaceComponent)
    }

}
