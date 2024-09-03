package com.story.api.application.feed

import com.story.api.application.component.ComponentCheckHandler
import com.story.core.common.annotation.HandlerAdapter
import com.story.core.domain.feed.FeedId
import com.story.core.domain.feed.FeedRemover
import com.story.core.domain.resource.ResourceId

@HandlerAdapter
class FeedRemoveHandler(
    private val feedRemover: FeedRemover,
    private val componentCheckHandler: ComponentCheckHandler,
) {

    suspend fun remove(
        workspaceId: String,
        componentId: String,
        ownerId: String,
        feedId: String,
    ) {
        componentCheckHandler.checkExistsComponent(
            workspaceId = workspaceId,
            resourceId = ResourceId.FEEDS,
            componentId = componentId,
        )

        feedRemover.remove(
            workspaceId = workspaceId,
            componentId = componentId,
            ownerId = ownerId,
            item = FeedId.of(feedId).toItem(),
        )
    }

}
