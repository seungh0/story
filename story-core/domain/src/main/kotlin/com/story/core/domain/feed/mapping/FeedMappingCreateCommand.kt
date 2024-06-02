package com.story.core.domain.feed.mapping

import com.story.core.domain.resource.ResourceId
import java.time.Duration

data class FeedMappingCreateCommand(
    val workspaceId: String,
    val feedComponentId: String,
    val sourceResourceId: ResourceId,
    val sourceComponentId: String,
    val subscriptionComponentId: String,
    val description: String,
    val retention: Duration,
)
