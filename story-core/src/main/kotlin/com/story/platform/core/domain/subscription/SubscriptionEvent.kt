package com.story.platform.core.domain.subscription

import com.story.platform.core.domain.event.BaseEvent
import com.story.platform.core.domain.event.EventAction
import com.story.platform.core.domain.event.EventKeyGenerator
import com.story.platform.core.domain.event.EventRecord
import com.story.platform.core.domain.resource.ResourceId

data class SubscriptionEvent(
    val subscriberId: String,
    val targetId: String,
) : BaseEvent {

    companion object {
        fun subscribed(
            workspaceId: String,
            componentId: String,
            subscriberId: String,
            targetId: String,
        ) = EventRecord(
            resourceId = ResourceId.SUBSCRIPTIONS,
            eventAction = EventAction.CREATED,
            workspaceId = workspaceId,
            componentId = componentId,
            eventKey = EventKeyGenerator.subscription(subscriberId = subscriberId, targetId = targetId),
            payload = SubscriptionEvent(
                subscriberId = subscriberId,
                targetId = targetId,
            )
        )

        fun unsubscribed(
            workspaceId: String,
            componentId: String,
            subscriberId: String,
            targetId: String,
        ) = EventRecord(
            resourceId = ResourceId.SUBSCRIPTIONS,
            eventAction = EventAction.DELETED,
            workspaceId = workspaceId,
            componentId = componentId,
            eventKey = EventKeyGenerator.subscription(subscriberId = subscriberId, targetId = targetId),
            payload = SubscriptionEvent(
                subscriberId = subscriberId,
                targetId = targetId,
            )
        )
    }

}
