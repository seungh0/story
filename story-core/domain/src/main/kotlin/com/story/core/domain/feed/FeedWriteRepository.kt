package com.story.core.domain.feed

import com.story.core.domain.resource.ResourceId
import java.time.Duration

interface FeedWriteRepository {

    suspend fun create(
        workspaceId: String,
        feedComponentId: String,
        slotId: Long,
        subscriberIds: Collection<String>,
        eventKey: String,
        retention: Duration,
        feedId: Long,
        sourceComponentId: String,
        sourceResourceId: ResourceId,
    )

    suspend fun delete(
        workspaceId: String,
        feedComponentId: String,
        subscriberId: String,
        feedId: Long,
    )

    suspend fun delete(
        workspaceId: String,
        feedComponentId: String,
        feedSubscribers: Collection<Feed>,
    )

}
