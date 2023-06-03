package com.story.platform.core.domain.subscription

interface SubscriptionCountRepository {

    suspend fun increase(key: SubscriptionCountKey, count: Long = 1L): Long

    suspend fun decrease(key: SubscriptionCountKey, count: Long = 1L): Long

    suspend fun get(key: SubscriptionCountKey): Long

    suspend fun delete(key: SubscriptionCountKey)

}
