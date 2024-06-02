package com.story.core.domain.feed.mapping

import com.story.core.domain.resource.ResourceId
import java.time.Duration

interface FeedMappingWriteRepository {

    suspend fun create(
        workspaceId: String,
        feedComponentId: String,
        sourceResourceId: ResourceId,
        sourceComponentId: String,
        subscriptionComponentId: String,
        description: String,
        retention: Duration,
    )

    suspend fun delete(
        workspaceId: String,
        feedComponentId: String,
        sourceResourceId: ResourceId,
        sourceComponentId: String,
        subscriptionComponentId: String,
    )

}
