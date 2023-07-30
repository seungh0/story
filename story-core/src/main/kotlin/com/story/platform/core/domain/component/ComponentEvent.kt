package com.story.platform.core.domain.component

import com.story.platform.core.domain.event.EventAction
import com.story.platform.core.domain.event.EventKeyGenerator
import com.story.platform.core.domain.event.EventRecord
import com.story.platform.core.domain.resource.ResourceId

data class ComponentEvent(
    val workspaceId: String,
    val resourceId: ResourceId,
    val componentId: String,
    val status: ComponentStatus,
) {

    companion object {
        fun updated(
            workspaceId: String,
            resourceId: ResourceId,
            componentId: String,
            status: ComponentStatus,
        ) = EventRecord(
            eventAction = EventAction.UPDATED,
            payload = ComponentEvent(
                workspaceId = workspaceId,
                resourceId = resourceId,
                componentId = componentId,
                status = status,
            ),
            eventKey = EventKeyGenerator.component(componentId = componentId),
        )
    }

}
