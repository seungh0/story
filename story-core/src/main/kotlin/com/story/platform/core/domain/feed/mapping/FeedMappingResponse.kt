package com.story.platform.core.domain.feed.mapping

import com.story.platform.core.domain.resource.ResourceId

data class FeedMappingResponse(
    val workspaceId: String,
    val feedComponentId: String,
    val sourceResourceId: ResourceId,
    val sourceComponentId: String,
    val subscriptionComponentId: String,
) {

    companion object {
        fun of(feedMapping: FeedMappingConfigurationReverse) = FeedMappingResponse(
            workspaceId = feedMapping.key.workspaceId,
            feedComponentId = feedMapping.key.feedComponentId,
            sourceResourceId = feedMapping.key.sourceResourceId,
            sourceComponentId = feedMapping.key.sourceComponentId,
            subscriptionComponentId = feedMapping.key.subscriptionComponentId,
        )
    }

}
