package com.story.core.domain.subscription

class SubscriptionCountMemoryRepository : SubscriptionCountRepository {

    private val subscriptionCountMap = mutableMapOf<SubscriptionCountPrimaryKey, Long>()

    fun clear() {
        subscriptionCountMap.clear()
    }

    fun findAll() = subscriptionCountMap

    override suspend fun increase(workspaceId: String, componentId: String, subscriberId: String, count: Long) {
        val key = SubscriptionCountPrimaryKey(
            workspaceId = workspaceId,
            componentId = componentId,
            subscriberId = subscriberId,
        )
        subscriptionCountMap[key] = (subscriptionCountMap[key] ?: 0) + count
    }

    override suspend fun decrease(workspaceId: String, componentId: String, subscriberId: String, count: Long) {
        val key = SubscriptionCountPrimaryKey(
            workspaceId = workspaceId,
            componentId = componentId,
            subscriberId = subscriberId,
        )
        subscriptionCountMap[key] = (subscriptionCountMap[key] ?: 0) - count
    }

    override suspend fun get(workspaceId: String, componentId: String, subscriberId: String): Long {
        val key = SubscriptionCountPrimaryKey(
            workspaceId = workspaceId,
            componentId = componentId,
            subscriberId = subscriberId,
        )
        return subscriptionCountMap[key] ?: 0L
    }

}
