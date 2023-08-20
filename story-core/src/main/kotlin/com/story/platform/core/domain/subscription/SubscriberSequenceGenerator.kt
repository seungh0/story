package com.story.platform.core.domain.subscription

import com.story.platform.core.infrastructure.redis.StringRedisRepository
import org.springframework.stereotype.Service

@Service
class SubscriberSequenceGenerator(
    private val subscriptionSequenceRepository: StringRedisRepository<SubscriberSequence, Long>,
) {

    suspend fun generate(
        workspaceId: String,
        componentId: String,
        targetId: String,
        count: Long = 1L,
    ) = subscriptionSequenceRepository.incrBy(
        key = SubscriberSequence(
            workspaceId = workspaceId,
            componentId = componentId,
            targetId = targetId,
        ),
        count = count,
    )

    suspend fun lastSequence(
        workspaceId: String,
        componentId: String,
        targetId: String,
    ) = subscriptionSequenceRepository.get(
        key = SubscriberSequence(
            workspaceId = workspaceId,
            componentId = componentId,
            targetId = targetId,
        )
    ) ?: START_SUBSCRIBER_SEQUENCE

    companion object {
        const val START_SUBSCRIBER_SEQUENCE = 1L
    }

}
