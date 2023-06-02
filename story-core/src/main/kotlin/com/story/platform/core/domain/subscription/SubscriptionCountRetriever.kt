package com.story.platform.core.domain.subscription

import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.support.cache.CacheType
import com.story.platform.core.support.cache.Cacheable
import org.springframework.stereotype.Service

@Service
class SubscriptionCountRetriever(
    private val subscribersCounterRepository: SubscribersCounterRepository,
    private val subscriptionsCounterRepository: SubscriptionsCounterRepository,
) {

    @Cacheable(
        cacheType = CacheType.SUBSCRIBERS_COUNT,
        key = "'serviceType:' + {#serviceType} + ':subscriptionType:' + {#subscriptionType} + ':targetId:' + {#targetId}",
    )
    suspend fun countSubscribers(
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        targetId: String,
    ): Long {
        val primaryKey = SubscribersCounterPrimaryKey(
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            targetId = targetId,
        )
        return subscribersCounterRepository.findById(primaryKey)?.count ?: 0L
    }

    suspend fun countSubscriptions(
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        subscriberId: String,
    ): Long {
        val primaryKey = SubscriptionsCounterPrimaryKey(
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            subscriberId = subscriberId,
        )
        return subscriptionsCounterRepository.findById(primaryKey)?.count ?: 0L
    }

}
