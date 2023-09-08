package com.story.platform.api.domain.feed

import com.story.platform.api.domain.component.ComponentCheckHandler
import com.story.platform.core.common.model.CursorResult
import com.story.platform.core.common.model.dto.CursorRequest
import com.story.platform.core.common.spring.HandlerAdapter
import com.story.platform.core.domain.event.BaseEvent
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
        cursorRequest: CursorRequest,
    ): CursorResult<FeedApiResponse<out BaseEvent>, String> {
        componentCheckHandler.checkExistsComponent(
            workspaceId = workspaceId,
            resourceId = ResourceId.FEEDS,
            componentId = feedComponentId,
        )

        val feeds = feedRetriever.listFeeds(
            workspaceId = workspaceId,
            feedComponentId = feedComponentId,
            subscriberId = subscriberId,
            cursorRequest = cursorRequest,
        )

        return CursorResult.of(
            data = feeds.data.map { feed -> FeedApiResponse.of(feed) },
            cursor = feeds.cursor,
        )
    }

}
