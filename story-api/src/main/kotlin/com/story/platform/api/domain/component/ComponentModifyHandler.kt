package com.story.platform.api.domain.component

import com.story.platform.core.common.spring.HandlerAdapter
import com.story.platform.core.domain.component.ComponentEventProducer
import com.story.platform.core.domain.component.ComponentModifier
import com.story.platform.core.domain.component.ComponentResponse
import com.story.platform.core.domain.component.ComponentStatus
import com.story.platform.core.domain.resource.ResourceId

@HandlerAdapter
class ComponentModifyHandler(
    private val componentModifier: ComponentModifier,
    private val componentEventProducer: ComponentEventProducer,
) {

    suspend fun patchComponent(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        description: String?,
        status: ComponentStatus?,
    ): ComponentResponse {
        val component = componentModifier.patchComponent(
            workspaceId = workspaceId,
            resourceId = resourceId,
            componentId = componentId,
            description = description,
            status = status,
        )

        componentEventProducer.publishUpdatedEvent(
            workspaceId = workspaceId,
            resourceId = resourceId,
            componentId = componentId,
        )
        return component
    }

}
