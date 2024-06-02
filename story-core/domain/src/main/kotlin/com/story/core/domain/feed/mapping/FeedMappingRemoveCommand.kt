package com.story.core.domain.feed.mapping

import com.story.core.domain.resource.ResourceId

data class FeedMappingRemoveCommand(
    val workspaceId: String,
    val feedComponentId: String,
    val sourceResourceId: ResourceId,
    val sourceComponentId: String,
    val subscriptionComponentId: String,
)
