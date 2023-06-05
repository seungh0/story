package com.story.platform.core.domain.subscription

import com.story.platform.core.common.enums.ServiceType
import org.springframework.stereotype.Service

@Service
class SubscriptionSubscribeHandler(
    private val subscriptionSubscriber: SubscriptionSubscriber,
    private val subscriptionCountManager: SubscriptionCountManager,
    private val subscriptionEventPublisher: SubscriptionEventPublisher,
) {

    suspend fun subscribe(
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        targetId: String,
        subscriberId: String,
        alarm: Boolean,
    ) {
        val isSubscribed = subscriptionSubscriber.subscribe(
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            targetId = targetId,
            subscriberId = subscriberId,
            alarm = alarm,
        )

        if (isSubscribed) {
            subscriptionCountManager.increase(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                targetId = targetId,
                subscriberId = subscriberId,
            )

            subscriptionEventPublisher.publishSubscriptionEvent(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                subscriberId = subscriberId,
                targetId = targetId,
            )
        }
    }

}
