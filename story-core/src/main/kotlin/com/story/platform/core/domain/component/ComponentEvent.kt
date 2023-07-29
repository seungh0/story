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
            component: Component,
        ) = EventRecord(
            eventAction = EventAction.UPDATED,
            payload = ComponentEvent(
                workspaceId = component.key.workspaceId,
                resourceId = component.key.resourceId,
                componentId = component.key.componentId,
                status = component.status,
            ),
            eventKey = EventKeyGenerator.component(componentId = component.key.componentId),
        )
    }

}
