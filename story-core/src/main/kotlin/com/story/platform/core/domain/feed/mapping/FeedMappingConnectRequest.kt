package com.story.platform.core.domain.feed.mapping

import com.story.platform.core.domain.resource.ResourceId

data class FeedMappingConnectRequest(
    val workspaceId: String,
    val feedComponentId: String,
    val sourceResourceId: ResourceId,
    val sourceComponentId: String,
    val subscriptionComponentId: String,
    val description: String,
) {

    fun toConfiguration() = FeedMappingConfiguration.of(
        workspaceId = workspaceId,
        feedComponentId = feedComponentId,
        sourceResourceId = sourceResourceId,
        sourceComponentId = sourceComponentId,
        subscriptionComponentId = subscriptionComponentId,
        description = description,
    )

}
