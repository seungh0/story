package com.story.core.domain.subscription

class SubscriberCountMemoryRepository : SubscriberCountRepository {

    private val subscriberCountMap = mutableMapOf<SubscriberCountPrimaryKey, Long>()

    fun clear() {
        subscriberCountMap.clear()
    }

    fun findAll() = subscriberCountMap

    override suspend fun increase(workspaceId: String, componentId: String, targetId: String, count: Long) {
        val key = SubscriberCountPrimaryKey(workspaceId = workspaceId, componentId = componentId, targetId = targetId)
        subscriberCountMap[key] = (subscriberCountMap[key] ?: 0L) + count
    }

    override suspend fun decrease(workspaceId: String, componentId: String, targetId: String, count: Long) {
        val key = SubscriberCountPrimaryKey(workspaceId = workspaceId, componentId = componentId, targetId = targetId)
        subscriberCountMap[key] = (subscriberCountMap[key] ?: 0L) - count
    }

    override suspend fun get(workspaceId: String, componentId: String, targetId: String): Long {
        val key = SubscriberCountPrimaryKey(workspaceId = workspaceId, componentId = componentId, targetId = targetId)
        return subscriberCountMap[key] ?: 0L
    }

}
