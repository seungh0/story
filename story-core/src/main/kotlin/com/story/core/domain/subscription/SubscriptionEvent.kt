package com.story.core.domain.subscription

import com.story.core.domain.event.BaseEvent
import com.story.core.domain.event.EventAction
import com.story.core.domain.event.EventRecord
import com.story.core.domain.resource.ResourceId
import java.time.LocalDateTime

data class SubscriptionEvent(
    val workspaceId: String,
    val resourceId: ResourceId,
    val componentId: String,
    val subscriberId: String,
    val targetId: String,
    val createdAt: LocalDateTime?,
) : BaseEvent {

    companion object {
        fun subscribed(
            workspaceId: String,
            componentId: String,
            subscriberId: String,
            targetId: String,
            createdAt: LocalDateTime,
        ) = EventRecord(
            eventAction = EventAction.CREATED,
            eventKey = SubscriptionEventKey(subscriberId = subscriberId, targetId = targetId).makeKey(),
            payload = SubscriptionEvent(
                workspaceId = workspaceId,
                resourceId = ResourceId.SUBSCRIPTIONS,
                componentId = componentId,
                subscriberId = subscriberId,
                targetId = targetId,
                createdAt = createdAt,
            )
        )

        fun unsubscribed(
            workspaceId: String,
            componentId: String,
            subscriberId: String,
            targetId: String,
        ) = EventRecord(
            eventAction = EventAction.REMOVED,
            eventKey = SubscriptionEventKey(subscriberId = subscriberId, targetId = targetId).makeKey(),
            payload = SubscriptionEvent(
                workspaceId = workspaceId,
                resourceId = ResourceId.SUBSCRIPTIONS,
                componentId = componentId,
                subscriberId = subscriberId,
                targetId = targetId,
                createdAt = null,
            )
        )
    }

}
