package com.story.platform.core.domain.subscription

import org.springframework.stereotype.Service

@Service
class SubscriptionCountRetriever(
    private val subscriptionCountRepository: SubscriptionCountRepository,
    private val subscriberCountRepository: SubscriberCountRepository,
) {

    suspend fun countSubscribers(
        workspaceId: String,
        componentId: String,
        targetId: String,
    ): Long {
        val key = SubscriberCountPrimaryKey(
            workspaceId = workspaceId,
            componentId = componentId,
            targetId = targetId,
        )
        return subscriberCountRepository.findById(key)?.count ?: 0L
    }

    suspend fun countSubscriptions(
        workspaceId: String,
        componentId: String,
        subscriberId: String,
    ): Long {
        val key = SubscriptionCountPrimaryKey(
            workspaceId = workspaceId,
            componentId = componentId,
            subscriberId = subscriberId,
        )
        return subscriptionCountRepository.findById(key)?.count ?: 0L
    }

}
