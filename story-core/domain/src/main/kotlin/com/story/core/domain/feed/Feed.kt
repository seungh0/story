package com.story.core.domain.feed

import com.story.core.domain.resource.ResourceId
import java.time.LocalDateTime

data class Feed(
    val workspaceId: String,
    val componentId: String,
    val ownerId: String,
    val sortKey: Long,
    val item: FeedItem,
    val createdAt: LocalDateTime,
) {

    fun makeFeedId(): String = FeedId(
        itemId = item.itemId,
        itemResourceId = item.resourceId,
        itemComponentId = item.componentId,
        channelId = item.channelId,
    ).makeKey()

}

data class FeedItem(
    val resourceId: ResourceId,
    val componentId: String,
    val channelId: String,
    val itemId: String,
)
