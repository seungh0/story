package com.story.core.domain.feed

interface FeedWriteRepository {

    suspend fun create(
        workspaceId: String,
        componentId: String,
        ownerIds: Collection<String>,
        sortKey: Long,
        item: FeedItem,
        options: FeedOptions,
    )

    suspend fun delete(
        workspaceId: String,
        componentId: String,
        ownerIds: Collection<String>,
        item: FeedItem,
        options: FeedOptions,
    )

}
