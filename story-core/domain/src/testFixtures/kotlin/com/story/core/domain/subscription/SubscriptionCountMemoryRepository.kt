package com.story.core.domain.subscription

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

class SubscriptionCountMemoryRepository : SubscriptionCountRepository {

    private val subscriptionCountMap = mutableMapOf<SubscriptionCountPrimaryKey, Long>()

    fun clear() {
        subscriptionCountMap.clear()
    }

    override suspend fun increase(key: SubscriptionCountPrimaryKey, count: Long) {
        subscriptionCountMap[key] = (subscriptionCountMap[key] ?: 0L) + count
    }

    override suspend fun decrease(key: SubscriptionCountPrimaryKey, count: Long) {
        subscriptionCountMap[key] = (subscriptionCountMap[key] ?: 0L) - count
    }

    override fun findAll(): Flow<SubscriptionCountEntity> {
        return subscriptionCountMap.map { (key, count) ->
            SubscriptionCountEntity(
                key = key,
                count = count,
            )
        }.asFlow()
    }

    override suspend fun findById(id: SubscriptionCountPrimaryKey): SubscriptionCountEntity? {
        return subscriptionCountMap[id]?.let {
            SubscriptionCountEntity(
                key = id,
                count = it,
            )
        }
    }

}
