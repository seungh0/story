package com.story.platform.core.domain.subscription

interface SubscribersCountRepository {

    suspend fun increase(key: SubscriberCountKey, count: Long = 1L): Long

    suspend fun decrease(key: SubscriberCountKey, count: Long = 1L): Long

    suspend fun get(key: SubscriberCountKey): Long

    suspend fun delete(key: SubscriberCountKey)

}
