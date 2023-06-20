package com.story.platform.core.domain.subscription

import com.story.platform.core.common.enums.EventType
import com.story.platform.core.domain.event.EventRecord

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
            eventType = EventType.SUBSCRIPTION_CREATED,
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
            eventType = EventType.SUBSCRIPTION_DELETED,
            payload = SubscriptionEvent(
                workspaceId = workspaceId,
                componentId = componentId,
                subscriberId = subscriberId,
                targetId = targetId,
            )
        )
    }

}
