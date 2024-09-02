package com.story.api.application.feed

import com.story.api.application.component.ComponentCheckHandler
import com.story.api.application.feed.payload.FeedPayloadHandlerFinder
import com.story.core.common.annotation.HandlerAdapter
import com.story.core.common.model.Slice
import com.story.core.domain.feed.Feed
import com.story.core.domain.feed.FeedItem
import com.story.core.domain.feed.FeedPayload
import com.story.core.domain.feed.FeedReader
import com.story.core.domain.resource.ResourceId

@HandlerAdapter
class FeedRetrieveHandler(
    private val feedReader: FeedReader,
    private val componentCheckHandler: ComponentCheckHandler,
    private val feedPayloadHandlerFinder: FeedPayloadHandlerFinder,
) {

    suspend fun listFeeds(
        workspaceId: String,
        componentId: String,
        ownerId: String,
        request: FeedListRequest,
    ): FeedListResponse {
        componentCheckHandler.checkExistsComponent(
            workspaceId = workspaceId,
            resourceId = ResourceId.FEEDS,
            componentId = componentId,
        )

        val feeds = feedReader.listFeeds(
            workspaceId = workspaceId,
            componentId = componentId,
            ownerId = ownerId,
            cursorRequest = request.toDecodedCursor(),
        )

        val feedPayloads = handleFeedPayloads(feeds = feeds, workspaceId = workspaceId, subscriberId = ownerId)

        return FeedListResponse.of(feeds = feeds, feedPayloads = feedPayloads)
    }

    private suspend fun handleFeedPayloads(
        feeds: Slice<Feed, String>,
        workspaceId: String,
        subscriberId: String,
    ): MutableMap<FeedItem, FeedPayload> {
        val feedPayloads = mutableMapOf<FeedItem, FeedPayload>()
        for ((resourceId, feesGroupByResource) in feeds.data.groupBy { feed -> feed.item.resourceId }) {
            val handler = feedPayloadHandlerFinder.get(resourceId = resourceId)
            feedPayloads.putAll(
                handler.handle(
                    workspaceId = workspaceId,
                    feeds = feesGroupByResource,
                    requestUserId = subscriberId
                )
            )
        }
        return feedPayloads
    }

}
