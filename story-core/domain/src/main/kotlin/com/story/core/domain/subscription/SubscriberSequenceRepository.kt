package com.story.core.domain.subscription

interface SubscriberSequenceRepository {

    suspend fun generate(
        workspaceId: String,
        componentId: String,
        targetId: String,
        count: Long = 1L,
    ): Long

    suspend fun getLastSequence(
        workspaceId: String,
        componentId: String,
        targetId: String,
    ): Long

}
