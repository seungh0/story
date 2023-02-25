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
        fun created(
            serviceType: ServiceType,
            subscriptionType: SubscriptionType,
            subscriberId: String,
            targetId: String,
        ) = SubscriptionEvent(
            eventType = SubscriptionEventType.UPSERT,
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            subscriberId = subscriberId,
            targetId = targetId,
        )

        fun deleted(
            serviceType: ServiceType,
            subscriptionType: SubscriptionType,
            subscriberId: String,
            targetId: String,
        ) = SubscriptionEvent(
            eventType = SubscriptionEventType.DELETE,
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            subscriberId = subscriberId,
            targetId = targetId,
        )
    }

}

enum class SubscriptionEventType {

    UPSERT,
    DELETE,

}
