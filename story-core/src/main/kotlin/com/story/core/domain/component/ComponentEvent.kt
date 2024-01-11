package com.story.core.domain.component

import com.story.core.domain.event.EventAction
import com.story.core.domain.event.EventKeyGenerator
import com.story.core.domain.event.EventRecord
import com.story.core.domain.resource.ResourceId

data class ComponentEvent(
    val workspaceId: String,
    val resourceId: ResourceId,
    val componentId: String,
) {

    companion object {
        fun updated(
            workspaceId: String,
            resourceId: ResourceId,
            componentId: String,
        ) = EventRecord(
            eventAction = EventAction.UPDATED,
            payload = ComponentEvent(
                workspaceId = workspaceId,
                resourceId = resourceId,
                componentId = componentId,
            ),
            eventKey = EventKeyGenerator.component(componentId = componentId),
        )
    }

}
