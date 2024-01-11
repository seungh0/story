package com.story.platform.api.application.component

import com.story.platform.core.common.annotation.HandlerAdapter
import com.story.platform.core.domain.component.ComponentNotExistsException
import com.story.platform.core.domain.component.ComponentRetriever
import com.story.platform.core.domain.resource.ResourceId

@HandlerAdapter
class ComponentRetrieveHandler(
    private val componentRetriever: ComponentRetriever,
) {

    suspend fun getComponent(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        request: ComponentGetApiRequest,
    ): ComponentApiResponse {
        val component = componentRetriever.getComponent(
            workspaceId = workspaceId,
            resourceId = resourceId,
            componentId = componentId
        )

        if (request.filterStatus != null && component.status != request.filterStatus) {
            throw ComponentNotExistsException(message = "워크스페이스($workspaceId)에 상태가 다른 컴포넌트($resourceId-$componentId)입니다. [filterStatus: ${request.filterStatus}, current: ${component.status}]")
        }

        return ComponentApiResponse.of(component)
    }

    suspend fun listComponents(
        workspaceId: String,
        resourceId: ResourceId,
        request: ComponentListApiRequest,
    ): ComponentListApiResponse {
        val components = componentRetriever.listComponents(
            workspaceId = workspaceId,
            resourceId = resourceId,
            cursorRequest = request.toCursor(),
        )
        return ComponentListApiResponse.of(components = components)
    }

}