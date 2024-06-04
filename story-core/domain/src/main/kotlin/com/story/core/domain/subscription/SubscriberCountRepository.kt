package com.story.core.domain.subscription

interface SubscriberCountRepository {

    suspend fun increase(workspaceId: String, componentId: String, targetId: String, count: Long = 1L)

    suspend fun decrease(workspaceId: String, componentId: String, targetId: String, count: Long = 1L)

    suspend fun get(workspaceId: String, componentId: String, targetId: String): Long

}
