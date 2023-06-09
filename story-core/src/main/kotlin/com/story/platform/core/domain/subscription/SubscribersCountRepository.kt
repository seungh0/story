package com.story.platform.core.domain.subscription

interface SubscribersCountRepository {

    suspend fun increase(key: SubscribersCountKey, count: Long = 1L): Long

    suspend fun decrease(key: SubscribersCountKey, count: Long = 1L): Long

    suspend fun get(key: SubscribersCountKey): Long

    suspend fun delete(key: SubscribersCountKey)

}
