package com.story.platform.core.domain.subscription

import org.springframework.stereotype.Service

@Service
class SubscriptionCountRetriever(
    private val subscribersCountRepository: SubscribersCountRepository,
    private val subscriptionsCountRepository: SubscriptionsCountRepository,
) {

    suspend fun countSubscribers(
        workspaceId: String,
        subscriptionType: SubscriptionType,
        targetId: String,
    ): Long {
        val key = SubscribersCountKey(
            workspaceId = workspaceId,
            subscriptionType = subscriptionType,
            targetId = targetId,
        )
        return subscribersCountRepository.get(key)
    }

    suspend fun countSubscriptions(
        workspaceId: String,
        subscriptionType: SubscriptionType,
        subscriberId: String,
    ): Long {
        val key = SubscriptionsCountKey(
            workspaceId = workspaceId,
            subscriptionType = subscriptionType,
            subscriberId = subscriberId,
        )
        return subscriptionsCountRepository.get(key)
    }

}
