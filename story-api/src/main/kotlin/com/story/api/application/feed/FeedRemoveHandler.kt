package com.story.api.application.feed

import com.story.core.common.annotation.HandlerAdapter
import com.story.core.domain.feed.FeedRemover

@HandlerAdapter
class FeedRemoveHandler(
    private val feedRemover: FeedRemover,
) {

    suspend fun remove(
        workspaceId: String,
        componentId: String,
        subscriberId: String,
        feedId: Long,
    ) {
        feedRemover.remove(
            workspaceId = workspaceId,
            feedComponentId = componentId,
            subscriberId = subscriberId,
            feedId = feedId,
        )
    }

}
