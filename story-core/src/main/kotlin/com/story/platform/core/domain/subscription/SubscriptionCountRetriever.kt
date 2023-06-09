package com.story.platform.core.domain.subscription

import com.story.platform.core.common.enums.ServiceType
import org.springframework.stereotype.Service

@Service
class SubscriptionCountRetriever(
    private val subscribersCountRepository: SubscribersCountRepository,
    private val subscriptionsCountRepository: SubscriptionsCountRepository,
) {

    suspend fun countSubscribers(
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        targetId: String,
    ): Long {
        val key = SubscribersCountKey(
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            targetId = targetId,
        )
        return subscribersCountRepository.get(key)
    }

    suspend fun countSubscriptions(
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        subscriberId: String,
    ): Long {
        val key = SubscriptionsCountKey(
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            subscriberId = subscriberId,
        )
        return subscriptionsCountRepository.get(key)
    }

}
