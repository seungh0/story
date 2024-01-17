package com.story.core.domain.subscription

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

class SubscriberCountMemoryRepository : SubscriberCountRepository {

    private val subscriberCountMap = mutableMapOf<SubscriberCountPrimaryKey, Long>()

    fun clear() {
        subscriberCountMap.clear()
    }

    override suspend fun increase(key: SubscriberCountPrimaryKey, count: Long) {
        subscriberCountMap[key] = (subscriberCountMap[key] ?: 0L) + count
    }

    override suspend fun decrease(key: SubscriberCountPrimaryKey, count: Long) {
        subscriberCountMap[key] = (subscriberCountMap[key] ?: 0L) - count
    }

    override fun findAll(): Flow<SubscriberCount> {
        return subscriberCountMap.map { (key, count) ->
            SubscriberCount(
                key = key,
                count = count,
            )
        }.asFlow()
    }

    override suspend fun findById(id: SubscriberCountPrimaryKey): SubscriberCount? {
        return subscriberCountMap[id]?.let {
            SubscriberCount(
                key = id,
                count = it,
            )
        }
    }

}
