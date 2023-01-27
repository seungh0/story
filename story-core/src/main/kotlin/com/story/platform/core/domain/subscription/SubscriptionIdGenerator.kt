package com.story.platform.core.domain.subscription

import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.support.redis.StringRedisRepository
import org.springframework.stereotype.Repository

@Repository
class SubscriptionIdGenerator(
    private val subscriptionIdRepository: StringRedisRepository<SubscriptionIdGenerateKey, Long>,
) {

    suspend fun generate(
        serviceType: ServiceType,
        subscriptionType: String,
        targetId: String,
    ) = subscriptionIdRepository.incr(
        key = SubscriptionIdGenerateKey(
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            targetId = targetId,
        )
    )

    suspend fun getLastSubscriptionId(
        serviceType: ServiceType,
        subscriptionType: String,
        targetId: String,
    ) = subscriptionIdRepository.get(
        key = SubscriptionIdGenerateKey(
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            targetId = targetId,
        )
    ) ?: INIT_SUBSCRIPTION_ID

    companion object {
        const val INIT_SUBSCRIPTION_ID = 0L
    }

}
