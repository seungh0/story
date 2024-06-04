package com.story.core.domain.subscription

import org.springframework.stereotype.Repository

@Repository
class SubscriberCountEntityRepository(
    private val subscriberCountCassandraRepository: SubscriberCountCassandraRepository,
) : SubscriberCountRepository {

    override suspend fun increase(workspaceId: String, componentId: String, targetId: String, count: Long) {
        val key = SubscriberCountPrimaryKey(workspaceId = workspaceId, componentId = componentId, targetId = targetId)
        return subscriberCountCassandraRepository.increase(key = key, count = count)
    }

    override suspend fun decrease(workspaceId: String, componentId: String, targetId: String, count: Long) {
        val key = SubscriberCountPrimaryKey(workspaceId = workspaceId, componentId = componentId, targetId = targetId)
        return subscriberCountCassandraRepository.decrease(key = key, count = count)
    }

    override suspend fun get(workspaceId: String, componentId: String, targetId: String): Long {
        val key = SubscriberCountPrimaryKey(workspaceId = workspaceId, componentId = componentId, targetId = targetId)
        return subscriberCountCassandraRepository.findById(key)?.count ?: 0L
    }

}
