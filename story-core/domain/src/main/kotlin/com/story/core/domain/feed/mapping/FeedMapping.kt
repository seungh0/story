package com.story.core.domain.feed.mapping

import com.story.core.domain.resource.ResourceId
import java.time.Duration

data class FeedMapping(
    val workspaceId: String,
    val feedComponentId: String,
    val sourceResourceId: ResourceId,
    val sourceComponentId: String,
    val subscriptionComponentId: String,
    val retention: Duration,
)
