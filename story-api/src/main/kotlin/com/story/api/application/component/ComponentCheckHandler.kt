package com.story.api.application.component

import com.story.core.common.annotation.HandlerAdapter
import com.story.core.domain.component.ComponentNotExistsException
import com.story.core.domain.component.ComponentRetriever
import com.story.core.domain.resource.ResourceId

@HandlerAdapter
class ComponentCheckHandler(
    private val componentRetriever: ComponentRetriever,
) {

    suspend fun checkExistsComponent(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
    ) {
        val component = componentRetriever.getComponent(
            workspaceId = workspaceId,
            resourceId = resourceId,
            componentId = componentId,
        )
            .orElseThrow { ComponentNotExistsException(message = "워크스페이스($workspaceId)에 등록되지 않은 컴포넌트($resourceId-$componentId)입니다") }

        if (!component.isActivated()) {
            throw ComponentNotExistsException(message = "비활성화된 컴포넌트($componentId)입니다. [워크스페이스: ($workspaceId), 리소스: ($resourceId)]")
        }
    }

}
