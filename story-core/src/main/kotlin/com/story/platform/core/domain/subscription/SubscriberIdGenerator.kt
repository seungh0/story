package com.story.platform.core.domain.subscription

import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.infrastructure.redis.StringRedisRepository
import org.springframework.stereotype.Repository

@Repository
class SubscriberIdGenerator(
    private val subscriptionIdRepository: StringRedisRepository<SubscriberIdGeneratorKey, Long>,
) {

    suspend fun generate(
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        targetId: String,
    ) = subscriptionIdRepository.incr(
        key = SubscriberIdGeneratorKey(
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            targetId = targetId,
        )
    )

    suspend fun getLastSubscriptionId(
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        targetId: String,
    ) = subscriptionIdRepository.get(
        key = SubscriberIdGeneratorKey(
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            targetId = targetId,
        )
    ) ?: INIT_SUBSCRIPTION_ID

    companion object {
        const val INIT_SUBSCRIPTION_ID = 1L
    }

}
