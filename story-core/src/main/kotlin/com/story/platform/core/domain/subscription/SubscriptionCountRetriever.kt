package com.story.platform.core.domain.subscription

import org.springframework.stereotype.Service

@Service
class SubscriptionCountRetriever(
    private val subscribersCountRepository: SubscribersCountRepository,
    private val subscriptionsCountRepository: SubscriptionsCountRepository,
) {

    suspend fun countSubscribers(
        workspaceId: String,
        componentId: String,
        targetId: String,
    ): Long {
        val key = SubscribersCountKey(
            workspaceId = workspaceId,
            componentId = componentId,
            targetId = targetId,
        )
        return subscribersCountRepository.get(key)
    }

    suspend fun countSubscriptions(
        workspaceId: String,
        componentId: String,
        subscriberId: String,
    ): Long {
        val key = SubscriptionsCountKey(
            workspaceId = workspaceId,
            componentId = componentId,
            subscriberId = subscriberId,
        )
        return subscriptionsCountRepository.get(key)
    }

}
