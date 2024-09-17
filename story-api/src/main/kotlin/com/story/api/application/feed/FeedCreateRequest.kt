package com.story.api.application.feed

import com.story.core.domain.feed.FeedItem
import com.story.core.domain.feed.FeedItemWithOption
import com.story.core.domain.resource.ResourceId

data class FeedCreateRequest(
    val priority: Long,
    val item: FeedItemCreateRequest,
) {

    fun toFeedItem() = FeedItemWithOption(
        item = FeedItem(
            resourceId = ResourceId.findByCode(item.resourceId),
            componentId = item.componentId,
            channelId = item.channelId,
            itemId = item.itemId,
        ),
        priority = priority,
    )

}
