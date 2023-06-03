package com.story.platform.core.domain.subscription

import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.infrastructure.redis.StringRedisRepository
import org.springframework.stereotype.Repository

@Repository
class SubscriberSequenceGenerator(
    private val subscriptionSequenceRepository: StringRedisRepository<SubscriberSequence, Long>,
) {

    suspend fun generate(
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        targetId: String,
    ) = subscriptionSequenceRepository.incr(
        key = SubscriberSequence(
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            targetId = targetId,
        )
    )

    suspend fun lastSequence(
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        targetId: String,
    ) = subscriptionSequenceRepository.get(
        key = SubscriberSequence(
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            targetId = targetId,
        )
    ) ?: START_SUBSCRIBER_SEQUENCE

    companion object {
        const val START_SUBSCRIBER_SEQUENCE = 1L
    }

}
