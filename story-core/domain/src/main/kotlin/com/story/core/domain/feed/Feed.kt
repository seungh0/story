package com.story.core.domain.feed

import com.story.core.domain.resource.ResourceId
import java.time.LocalDateTime

data class Feed(
    val workspaceId: String,
    val componentId: String,
    val ownerId: String,
    val priority: Long,
    val item: FeedItem,
    val extra: Map<String, Any>,
    val createdAt: LocalDateTime,
) {

    fun makeFeedId(): String = FeedId(
        itemResourceId = item.resourceId,
        itemComponentId = item.componentId,
        channelId = item.channelId,
        itemId = item.itemId,
    ).makeKey()

}

data class FeedItem(
    val resourceId: ResourceId,
    val componentId: String,
    val channelId: String,
    val itemId: String,
)
