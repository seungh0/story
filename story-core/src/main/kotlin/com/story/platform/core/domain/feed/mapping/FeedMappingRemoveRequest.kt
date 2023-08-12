package com.story.platform.core.domain.feed.mapping

import com.story.platform.core.domain.resource.ResourceId

data class FeedMappingRemoveRequest(
    val workspaceId: String,
    val feedComponentId: String,
    val resourceId: ResourceId,
    val componentId: String,
    val subscriptionComponentId: String,
) {

    fun toConfigurationPrimaryKey() = FeedMappingConfigurationPrimaryKey(
        workspaceId = workspaceId,
        feedComponentId = feedComponentId,
        sourceResourceId = resourceId,
        sourceComponentId = componentId,
        subscriptionComponentId = subscriptionComponentId,
    )

}
