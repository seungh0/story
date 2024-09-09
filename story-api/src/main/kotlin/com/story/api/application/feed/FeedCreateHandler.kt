package com.story.api.application.feed

import com.story.api.application.component.ComponentCheckHandler
import com.story.core.common.annotation.HandlerAdapter
import com.story.core.domain.feed.FeedCreator
import com.story.core.domain.feed.FeedOptions
import com.story.core.domain.resource.ResourceId

@HandlerAdapter
class FeedCreateHandler(
    private val feedCreator: FeedCreator,
    private val componentCheckHandler: ComponentCheckHandler,
) {

    suspend fun createFeeds(
        workspaceId: String,
        componentId: String,
        ownerId: String,
        request: FeedListCreateRequest,
    ) {
        componentCheckHandler.checkExistsComponent(
            workspaceId = workspaceId,
            resourceId = ResourceId.FEEDS,
            componentId = componentId,
        )

        feedCreator.create(
            workspaceId = workspaceId,
            componentId = componentId,
            ownerId = ownerId,
            items = request.feeds.map { feed -> feed.toFeedItem() },
            options = FeedOptions(retention = request.options.retention)
        )
    }

}
