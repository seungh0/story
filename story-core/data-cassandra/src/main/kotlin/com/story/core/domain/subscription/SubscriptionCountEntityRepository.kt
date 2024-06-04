package com.story.core.domain.subscription

import org.springframework.stereotype.Repository

@Repository
class SubscriptionCountEntityRepository(
    private val subscriptionCountCassandraRepository: SubscriptionCountCassandraRepository,
) : SubscriptionCountRepository {

    override suspend fun increase(workspaceId: String, componentId: String, subscriberId: String, count: Long) {
        val key = SubscriptionCountPrimaryKey(
            workspaceId = workspaceId,
            componentId = componentId,
            subscriberId = subscriberId,
        )
        return subscriptionCountCassandraRepository.increase(
            key = key,
            count = count
        )
    }

    override suspend fun decrease(workspaceId: String, componentId: String, subscriberId: String, count: Long) {
        val key = SubscriptionCountPrimaryKey(
            workspaceId = workspaceId,
            componentId = componentId,
            subscriberId = subscriberId,
        )
        return subscriptionCountCassandraRepository.decrease(
            key = key,
            count = count,
        )
    }

    override suspend fun get(workspaceId: String, componentId: String, subscriberId: String): Long {
        val key = SubscriptionCountPrimaryKey(
            workspaceId = workspaceId,
            componentId = componentId,
            subscriberId = subscriberId,
        )
        return subscriptionCountCassandraRepository.findById(key)?.count ?: 0L
    }

}
