package com.story.platform.core.domain.subscription

import com.story.platform.core.common.enums.EventType
import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.domain.event.EventRecord

data class SubscriptionEvent(
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
        ) = EventRecord(
            eventType = EventType.SUBSCRIPTION_CREATED,
            payload = SubscriptionEvent(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                subscriberId = subscriberId,
                targetId = targetId,
            )
        )

        fun unsubscribed(
            serviceType: ServiceType,
            subscriptionType: SubscriptionType,
            subscriberId: String,
            targetId: String,
        ) = EventRecord(
            eventType = EventType.SUBSCRIPTION_DELETED,
            payload = SubscriptionEvent(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                subscriberId = subscriberId,
                targetId = targetId,
            )
        )
    }

}
