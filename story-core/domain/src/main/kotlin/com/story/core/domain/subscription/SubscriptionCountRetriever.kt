package com.story.core.domain.subscription

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
        return subscriberCountRepository.get(
            workspaceId = workspaceId,
            componentId = componentId,
            targetId = targetId,
        )
    }

    suspend fun countTargets(
        workspaceId: String,
        componentId: String,
        subscriberId: String,
    ): Long {
        return subscriptionCountRepository.get(
            workspaceId = workspaceId,
            componentId = componentId,
            subscriberId = subscriberId,
        )
    }

}
