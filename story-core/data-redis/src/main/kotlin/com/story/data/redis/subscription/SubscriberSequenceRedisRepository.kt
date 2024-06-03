package com.story.data.redis.subscription

import com.story.core.domain.subscription.SubscriberSequenceRepository
import com.story.core.infrastructure.redis.StringRedisRepository
import org.springframework.stereotype.Repository

@Repository
class SubscriberSequenceRedisRepository(
    private val subscriptionSequenceRepository: StringRedisRepository<SubscriberSequenceKey, Long>,
) : SubscriberSequenceRepository {

    override suspend fun generate(
        workspaceId: String,
        componentId: String,
        targetId: String,
        count: Long,
    ) = subscriptionSequenceRepository.incrBy(
        key = SubscriberSequenceKey(
            workspaceId = workspaceId,
            componentId = componentId,
            targetId = targetId,
        ),
        count = count,
    )

    override suspend fun getLastSequence(
        workspaceId: String,
        componentId: String,
        targetId: String,
    ) = subscriptionSequenceRepository.get(
        key = SubscriberSequenceKey(
            workspaceId = workspaceId,
            componentId = componentId,
            targetId = targetId,
        )
    ) ?: START_SUBSCRIBER_SEQUENCE

    companion object {
        const val START_SUBSCRIBER_SEQUENCE = 1L
    }

}
