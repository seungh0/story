package com.story.core.domain.feed.mapping

import com.story.core.domain.resource.ResourceId

data class FeedMappingRemoveRequest(
    val workspaceId: String,
    val feedComponentId: String,
    val resourceId: ResourceId,
    val componentId: String,
    val subscriptionComponentId: String,
) {

    fun toConfigurationPrimaryKey() = FeedMappingPrimaryKey(
        workspaceId = workspaceId,
        feedComponentId = feedComponentId,
        sourceResourceId = resourceId,
        sourceComponentId = componentId,
        subscriptionComponentId = subscriptionComponentId,
    )

}
