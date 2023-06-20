package com.story.platform.core.domain.subscription

import com.story.platform.core.infrastructure.redis.StringRedisRepository
import org.springframework.stereotype.Repository

@Repository
class SubscriberSequenceGenerator(
    private val subscriptionSequenceRepository: StringRedisRepository<SubscriberSequence, Long>,
) {

    suspend fun generate(
        workspaceId: String,
        subscriptionType: SubscriptionType,
        targetId: String,
    ) = subscriptionSequenceRepository.incr(
        key = SubscriberSequence(
            workspaceId = workspaceId,
            subscriptionType = subscriptionType,
            targetId = targetId,
        )
    )

    suspend fun lastSequence(
        workspaceId: String,
        subscriptionType: SubscriptionType,
        targetId: String,
    ) = subscriptionSequenceRepository.get(
        key = SubscriberSequence(
            workspaceId = workspaceId,
            subscriptionType = subscriptionType,
            targetId = targetId,
        )
    ) ?: START_SUBSCRIBER_SEQUENCE

    companion object {
        const val START_SUBSCRIBER_SEQUENCE = 1L
    }

}
