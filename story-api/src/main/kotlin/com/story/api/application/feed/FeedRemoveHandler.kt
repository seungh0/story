package com.story.api.application.feed

import com.story.core.common.annotation.HandlerAdapter
import com.story.core.domain.feed.FeedId
import com.story.core.domain.feed.FeedOptions
import com.story.core.domain.feed.FeedRemover
import java.time.Duration

@HandlerAdapter
class FeedRemoveHandler(
    private val feedRemover: FeedRemover,
) {

    suspend fun remove(
        workspaceId: String,
        componentId: String,
        ownerId: String,
        feedId: String,
    ) {
        feedRemover.remove(
            workspaceId = workspaceId,
            componentId = componentId,
            ownerIds = setOf(ownerId),
            item = FeedId.of(feedId).toItem(),
            options = FeedOptions(retention = Duration.ofDays(100)) // TODO: 어떻게?
        )
    }

}
