package com.story.platform.api.domain.component

import com.story.platform.core.common.error.ErrorCode
import com.story.platform.core.common.error.NotFoundException
import com.story.platform.core.domain.component.ComponentRetriever
import com.story.platform.core.domain.resource.ResourceId
import org.springframework.stereotype.Service

@Service
class ComponentHandler(
    private val componentRetriever: ComponentRetriever,
) {

    suspend fun validateComponent(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
    ) {
        val component = componentRetriever.getComponent(
            workspaceId = workspaceId,
            resourceId = resourceId,
            componentId = componentId,
        )
        if (!component.isActivated()) {
            throw NotFoundException(
                message = "비활성화된 컴포넌트($componentId)입니다. [워크스페이스: ($workspaceId), 리소스: ($resourceId)]",
                errorCode = ErrorCode.E404_NOT_FOUND_COMPONENT,
            )
        }
    }

}
