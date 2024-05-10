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
) {

    companion object {
        fun of(feedMapping: FeedMappingReverse) = FeedMapping(
            workspaceId = feedMapping.key.workspaceId,
            feedComponentId = feedMapping.key.feedComponentId,
            sourceResourceId = feedMapping.key.sourceResourceId,
            sourceComponentId = feedMapping.key.sourceComponentId,
            subscriptionComponentId = feedMapping.key.subscriptionComponentId,
            retention = feedMapping.retention,
        )
    }

}
