package com.story.api.application.feed

import com.story.api.application.component.ComponentCheckHandler
import com.story.api.application.feed.payload.FeedPayloadHandlerFinder
import com.story.core.common.annotation.HandlerAdapter
import com.story.core.domain.feed.FeedPayload
import com.story.core.domain.feed.FeedRetriever
import com.story.core.domain.resource.ResourceId

@HandlerAdapter
class FeedRetrieveHandler(
    private val feedRetriever: FeedRetriever,
    private val componentCheckHandler: ComponentCheckHandler,
    private val feedPayloadHandlerFinder: FeedPayloadHandlerFinder,
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

        val feedPayloads = mutableMapOf<Long, FeedPayload>()
        feeds.data.groupBy { feed -> feed.sourceResourceId }.forEach { (resourceId, feeds) ->
            val handler = feedPayloadHandlerFinder.get(resourceId = resourceId)
            feedPayloads.putAll(handler.handle(workspaceId = workspaceId, feeds = feeds, requestAccountId = subscriberId))
        }

        return FeedListApiResponse.of(feeds = feeds, feedPayloads = feedPayloads)
    }

}
