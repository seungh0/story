package com.story.platform.api.application.feed

import com.story.platform.api.application.component.ComponentCheckHandler
import com.story.platform.core.common.annotation.HandlerAdapter
import com.story.platform.core.domain.feed.FeedRetriever
import com.story.platform.core.domain.resource.ResourceId

@HandlerAdapter
class FeedRetrieveHandler(
    private val feedRetriever: FeedRetriever,
    private val componentCheckHandler: ComponentCheckHandler,
) {

    suspend fun listFeeds(
        workspaceId: String,
        feedComponentId: String,
        subscriberId: String,
        request: FeedListApiRequest,
    ): FeedListApiResponse {
        componentCheckHandler.checkExistsComponent(
            workspaceId = workspaceId,
            resourceId = ResourceId.FEEDS,
            componentId = feedComponentId,
        )

        val feeds = feedRetriever.listFeeds(
            workspaceId = workspaceId,
            feedComponentId = feedComponentId,
            subscriberId = subscriberId,
            cursorRequest = request.toCursor(),
        )

        return FeedListApiResponse.of(feeds = feeds)
    }

}
