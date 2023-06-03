package com.story.platform.core.handler.subscription

import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.domain.subscription.SubscriptionCountManager
import com.story.platform.core.domain.subscription.SubscriptionEventPublisher
import com.story.platform.core.domain.subscription.SubscriptionSubscriber
import com.story.platform.core.domain.subscription.SubscriptionType
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
        val isNewSubscriber = subscriptionSubscriber.subscribe(
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            targetId = targetId,
            subscriberId = subscriberId,
            alarm = alarm,
        )

        if (isNewSubscriber) {
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
