package com.story.core.domain.subscription

interface SubscriptionCountRepository {

    suspend fun increase(workspaceId: String, componentId: String, subscriberId: String, count: Long = 1L)

    suspend fun decrease(workspaceId: String, componentId: String, subscriberId: String, count: Long = 1L)

    suspend fun get(workspaceId: String, componentId: String, subscriberId: String): Long

}
