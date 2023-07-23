package com.story.platform.core.domain.subscription

import com.story.platform.core.domain.event.EventAction
import com.story.platform.core.domain.event.EventKeyGenerator
import com.story.platform.core.domain.event.EventRecord
import com.story.platform.core.domain.resource.ResourceId

data class SubscriptionEvent(
    val workspaceId: String,
    val componentId: String,
    val subscriberId: String,
    val targetId: String,
) {

    companion object {
        fun subscribed(
            workspaceId: String,
            componentId: String,
            subscriberId: String,
            targetId: String,
        ) = EventRecord(
            resourceId = ResourceId.SUBSCRIPTIONS,
            eventAction = EventAction.CREATED,
            eventKey = EventKeyGenerator.subscription(subscriberId = subscriberId, targetId = targetId),
            payload = SubscriptionEvent(
                workspaceId = workspaceId,
                componentId = componentId,
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
            eventKey = EventKeyGenerator.subscription(subscriberId = subscriberId, targetId = targetId),
            payload = SubscriptionEvent(
                workspaceId = workspaceId,
                componentId = componentId,
                subscriberId = subscriberId,
                targetId = targetId,
            )
        )
    }

}
