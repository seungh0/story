package com.story.core.domain.component

import com.story.core.domain.resource.ResourceId
import org.springframework.stereotype.Service

@Service
class ComponentCreator(
    private val componentReadRepository: ComponentReadRepository,
    private val componentWriteRepository: ComponentWriteRepository,
) {

    suspend fun createComponent(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        description: String,
    ): Component {
        val exists = componentReadRepository.existsById(
            workspaceId = workspaceId,
            resourceId = resourceId,
            componentId = componentId,
        )

        if (exists) {
            throw ComponentAlreadyExistsException(message = "워크스페이스($workspaceId)에 이미 등록된 리소스($resourceId) 컴포넌트($componentId)입니다.")
        }

        return componentWriteRepository.create(
            workspaceId = workspaceId,
            resourceId = resourceId,
            componentId = componentId,
            description = description,
            status = ComponentStatus.ENABLED
        )
    }

}