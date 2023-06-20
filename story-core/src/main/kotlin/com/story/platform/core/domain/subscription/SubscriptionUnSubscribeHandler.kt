package com.story.platform.core.domain.subscription

import org.springframework.stereotype.Service

@Service
class SubscriptionUnSubscribeHandler(
    private val subscriptionUnSubscriber: SubscriptionUnSubscriber,
    private val subscriptionCountManager: SubscriptionCountManager,
    private val subscriptionEventPublisher: SubscriptionEventPublisher,
) {

    suspend fun unsubscribe(
        workspaceId: String,
        subscriptionType: SubscriptionType,
        targetId: String,
        subscriberId: String,
    ) {
        val isUnsubscribed = subscriptionUnSubscriber.unsubscribe(
            workspaceId = workspaceId,
            subscriptionType = subscriptionType,
            targetId = targetId,
            subscriberId = subscriberId,
        )

        if (isUnsubscribed) {
            subscriptionCountManager.decrease(
                workspaceId = workspaceId,
                subscriptionType = subscriptionType,
                targetId = targetId,
                subscriberId = subscriberId,
            )

            subscriptionEventPublisher.publishUnsubscriptionEvent(
                workspaceId = workspaceId,
                subscriptionType = subscriptionType,
                subscriberId = subscriberId,
                targetId = targetId,
            )
        }
    }

}
