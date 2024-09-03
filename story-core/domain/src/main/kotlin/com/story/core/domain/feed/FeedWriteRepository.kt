package com.story.core.domain.feed

interface FeedWriteRepository {

    suspend fun create(
        workspaceId: String,
        componentId: String,
        ownerIds: Collection<String>,
        priority: Long,
        item: FeedItem,
        options: FeedOptions,
    )

    suspend fun delete(
        workspaceId: String,
        componentId: String,
        ownerId: String,
        item: FeedItem,
    )

    suspend fun clearByChannel(
        workspaceId: String,
        componentId: String,
        ownerId: String,
        channelId: String,
    )

}
