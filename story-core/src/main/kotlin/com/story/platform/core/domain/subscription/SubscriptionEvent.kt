package com.story.platform.core.domain.subscription

import com.story.platform.core.common.enums.ServiceType

data class SubscriptionEvent(
    val eventType: SubscriptionEventType,
    val serviceType: ServiceType,
    val subscriptionType: SubscriptionType,
    val subscriberId: String,
    val targetId: String,
) {

    companion object {
        fun subscribed(
            serviceType: ServiceType,
            subscriptionType: SubscriptionType,
            subscriberId: String,
            targetId: String,
        ) = SubscriptionEvent(
            eventType = SubscriptionEventType.SUBSCRIBED,
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            subscriberId = subscriberId,
            targetId = targetId,
        )

        fun unsubscribed(
            serviceType: ServiceType,
            subscriptionType: SubscriptionType,
            subscriberId: String,
            targetId: String,
        ) = SubscriptionEvent(
            eventType = SubscriptionEventType.UNSUBSCRIBED,
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            subscriberId = subscriberId,
            targetId = targetId,
        )
    }

}
