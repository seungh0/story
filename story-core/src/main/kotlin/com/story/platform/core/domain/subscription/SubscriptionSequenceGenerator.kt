package com.story.platform.core.domain.subscription

import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.support.redis.StringRedisRepository
import org.springframework.stereotype.Repository

@Repository
class SubscriptionSequenceGenerator(
    private val subscriptionSequenceRepository: StringRedisRepository<SubscriptionIdKey, Long>,
) {

    suspend fun generate(
        serviceType: ServiceType,
        subscriptionType: String,
        targetId: String,
    ) = subscriptionSequenceRepository.incr(
        key = SubscriptionIdKey(
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            targetId = targetId,
        )
    )

    suspend fun getLastSequence(
        serviceType: ServiceType,
        subscriptionType: String,
        targetId: String,
    ) = subscriptionSequenceRepository.get(
        key = SubscriptionIdKey(
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            targetId = targetId,
        )
    ) ?: INIT_SUBSCRIPTION_SEQUENCE

    companion object {
        const val INIT_SUBSCRIPTION_SEQUENCE = 0L
    }

}
