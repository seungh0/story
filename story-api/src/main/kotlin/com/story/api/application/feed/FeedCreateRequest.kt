package com.story.api.application.feed

import com.story.core.domain.resource.ResourceId
import jakarta.validation.constraints.Size

data class FeedListCreateRequest(
    @field:Size(min = 1, max = 100)
    val feeds: List<FeedCreateRequest>,
)

data class FeedCreateRequest(
    val priority: Long,
    val item: FeedItemCreateRequest,
)

data class FeedItemCreateRequest(
    val resourceId: ResourceId,
    val componentId: String,
    val channelId: String,
    val itemId: String,
)
