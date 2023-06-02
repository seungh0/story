package com.story.platform.core.handler.subscription

import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.domain.subscription.SubscriptionCounterManager
import com.story.platform.core.domain.subscription.SubscriptionEventPublisher
import com.story.platform.core.domain.subscription.SubscriptionType
import com.story.platform.core.domain.subscription.SubscriptionUnSubscriber
import org.springframework.stereotype.Service

@Service
class SubscriptionUnSubscribeHandler(
    private val subscriptionUnSubscriber: SubscriptionUnSubscriber,
    private val subscriptionCounterManager: SubscriptionCounterManager,
    private val subscriptionEventPublisher: SubscriptionEventPublisher,
) {

    suspend fun unsubscribe(
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        targetId: String,
        subscriberId: String,
    ) {
        val isUnSubscribe = subscriptionUnSubscriber.unsubscribe(
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            targetId = targetId,
            subscriberId = subscriberId,
        )

        if (isUnSubscribe) {
            subscriptionCounterManager.decrease(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                targetId = targetId,
                subscriberId = subscriberId,
            )

            subscriptionEventPublisher.publishUnsubscriptionEvent(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                subscriberId = subscriberId,
                targetId = targetId,
            )
        }
    }

}
