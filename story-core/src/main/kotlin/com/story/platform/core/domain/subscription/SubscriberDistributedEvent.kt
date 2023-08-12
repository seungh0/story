package com.story.platform.core.domain.subscription

import com.story.platform.core.common.json.toJson
import com.story.platform.core.domain.event.BaseEvent
import com.story.platform.core.domain.event.EventAction
import com.story.platform.core.domain.resource.ResourceId

data class SubscriberDistributedEvent(
    val workspaceId: String,
    val feedComponentId: String,
    val sourceResourceId: ResourceId,
    val sourceComponentId: String,
    val subscriptionComponentId: String,
    val targetId: String,
    val slotId: Long,
    val payloadJson: String,
    val eventAction: EventAction,
    val eventKey: String,
    val eventId: Long,
) {

    companion object {
        fun <T : BaseEvent> of(
            workspaceId: String,
            feedComponentId: String,
            sourceResourceId: ResourceId,
            sourceComponentId: String,
            subscriptionComponentId: String,
            targetId: String,
            slotId: Long,
            payload: T,
            eventAction: EventAction,
            eventKey: String,
            eventId: Long,
        ) = SubscriberDistributedEvent(
            workspaceId = workspaceId,
            feedComponentId = feedComponentId,
            sourceResourceId = sourceResourceId,
            sourceComponentId = sourceComponentId,
            subscriptionComponentId = subscriptionComponentId,
            targetId = targetId,
            slotId = slotId,
            payloadJson = payload.toJson(),
            eventAction = eventAction,
            eventKey = eventKey,
            eventId = eventId,
        )
    }

}
