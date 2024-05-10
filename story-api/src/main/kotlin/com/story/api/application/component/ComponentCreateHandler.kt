package com.story.api.application.component

import com.story.core.common.annotation.HandlerAdapter
import com.story.core.domain.component.Component
import com.story.core.domain.component.ComponentCreator
import com.story.core.domain.resource.ResourceId

@HandlerAdapter
class ComponentCreateHandler(
    private val componentCreator: ComponentCreator,
) {

    suspend fun createComponent(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        description: String,
    ): Component {
        return componentCreator.createComponent(
            workspaceId = workspaceId,
            resourceId = resourceId,
            componentId = componentId,
            description = description,
        )
    }

}
