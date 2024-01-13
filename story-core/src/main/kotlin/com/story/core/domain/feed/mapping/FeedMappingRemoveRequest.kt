package com.story.core.domain.feed.mapping

import com.story.core.domain.resource.ResourceId

data class FeedMappingRemoveRequest(
    val workspaceId: String,
    val feedComponentId: String,
    val sourceResourceId: ResourceId,
    val sourceComponentId: String,
    val subscriptionComponentId: String,
) {

    fun toConfigurationPrimaryKey() = FeedMappingPrimaryKey(
        workspaceId = workspaceId,
        feedComponentId = feedComponentId,
        sourceResourceId = sourceResourceId,
        sourceComponentId = sourceComponentId,
        subscriptionComponentId = subscriptionComponentId,
    )

}
