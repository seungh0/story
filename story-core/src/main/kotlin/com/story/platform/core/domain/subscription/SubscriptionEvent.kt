package com.story.platform.core.domain.subscription

import com.story.platform.core.common.enums.EventType
import com.story.platform.core.domain.event.EventRecord

data class SubscriptionEvent(
    val workspaceId: String,
    val subscriptionType: SubscriptionType,
    val subscriberId: String,
    val targetId: String,
) {

    companion object {
        fun subscribed(
            workspaceId: String,
            subscriptionType: SubscriptionType,
            subscriberId: String,
            targetId: String,
        ) = EventRecord(
            eventType = EventType.SUBSCRIPTION_CREATED,
            payload = SubscriptionEvent(
                workspaceId = workspaceId,
                subscriptionType = subscriptionType,
                subscriberId = subscriberId,
                targetId = targetId,
            )
        )

        fun unsubscribed(
            workspaceId: String,
            subscriptionType: SubscriptionType,
            subscriberId: String,
            targetId: String,
        ) = EventRecord(
            eventType = EventType.SUBSCRIPTION_DELETED,
            payload = SubscriptionEvent(
                workspaceId = workspaceId,
                subscriptionType = subscriptionType,
                subscriberId = subscriberId,
                targetId = targetId,
            )
        )
    }

}
