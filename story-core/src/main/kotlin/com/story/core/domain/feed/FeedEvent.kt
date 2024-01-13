package com.story.core.domain.feed

import com.story.core.common.json.toJson
import com.story.core.domain.event.BaseEvent
import com.story.core.domain.event.EventAction
import com.story.core.domain.event.EventRecord
import com.story.core.domain.resource.ResourceId

data class FeedEvent(
    val workspaceId: String,
    val feedComponentId: String,
    val sourceResourceId: ResourceId,
    val sourceComponentId: String,
    val subscriptionComponentId: String,
    val targetId: String,
    val slotId: Long,
    val payloadJson: String,
) {

    companion object {
        fun <T : BaseEvent> of(
            eventAction: EventAction,
            eventKey: String,
            workspaceId: String,
            feedComponentId: String,
            sourceResourceId: ResourceId,
            sourceComponentId: String,
            subscriptionComponentId: String,
            targetId: String,
            slotId: Long,
            payload: T,
        ) = EventRecord(
            eventAction = eventAction,
            eventKey = eventKey,
            payload = FeedEvent(
                workspaceId = workspaceId,
                feedComponentId = feedComponentId,
                sourceResourceId = sourceResourceId,
                sourceComponentId = sourceComponentId,
                subscriptionComponentId = subscriptionComponentId,
                targetId = targetId,
                slotId = slotId,
                payloadJson = payload.toJson(),
            )
        )
    }

}