package com.story.api.application.feed

import com.story.core.domain.feed.FeedItem
import com.story.core.domain.feed.FeedItemWithOption
import com.story.core.domain.resource.ResourceId
import jakarta.validation.Valid
import jakarta.validation.constraints.Size
import java.time.Duration

data class FeedListCreateRequest(
    @field:Valid
    @field:Size(min = 1, max = 100)
    val feeds: List<FeedCreateRequest>,
    val options: FeedItemOptionsCreateRequest,
)

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

data class FeedItemCreateRequest(
    val resourceId: String,
    val componentId: String,
    val channelId: String,
    val itemId: String,
)

data class FeedItemOptionsCreateRequest(
    val retention: Duration,
)
