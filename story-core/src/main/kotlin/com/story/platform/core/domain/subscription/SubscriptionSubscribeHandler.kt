package com.story.platform.core.domain.subscription

import org.springframework.stereotype.Service

@Service
class SubscriptionSubscribeHandler(
    private val subscriptionSubscriber: SubscriptionSubscriber,
    private val subscriptionCountManager: SubscriptionCountManager,
    private val subscriptionEventPublisher: SubscriptionEventPublisher,
) {

    suspend fun subscribe(
        workspaceId: String,
        subscriptionType: SubscriptionType,
        targetId: String,
        subscriberId: String,
        alarm: Boolean,
    ) {
        val isSubscribed = subscriptionSubscriber.subscribe(
            workspaceId = workspaceId,
            subscriptionType = subscriptionType,
            targetId = targetId,
            subscriberId = subscriberId,
            alarm = alarm,
        )

        if (isSubscribed) {
            subscriptionCountManager.increase(
                workspaceId = workspaceId,
                subscriptionType = subscriptionType,
                targetId = targetId,
                subscriberId = subscriberId,
            )

            subscriptionEventPublisher.publishSubscriptionEvent(
                workspaceId = workspaceId,
                subscriptionType = subscriptionType,
                subscriberId = subscriberId,
                targetId = targetId,
            )
        }
    }

}
