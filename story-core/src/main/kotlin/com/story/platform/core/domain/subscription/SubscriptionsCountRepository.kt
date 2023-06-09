package com.story.platform.core.domain.subscription

interface SubscriptionsCountRepository {

    suspend fun increase(key: SubscriptionsCountKey, count: Long = 1L): Long

    suspend fun decrease(key: SubscriptionsCountKey, count: Long = 1L): Long

    suspend fun get(key: SubscriptionsCountKey): Long

    suspend fun delete(key: SubscriptionsCountKey)

}
