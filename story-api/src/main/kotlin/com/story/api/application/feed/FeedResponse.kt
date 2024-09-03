package com.story.api.application.feed

import com.story.core.domain.feed.Feed
import com.story.core.domain.feed.FeedPayload

data class FeedResponse(
    val feedId: String,
    val item: FeedItemResponse,
) {

    companion object {
        fun of(feed: Feed, payload: FeedPayload) = FeedResponse(
            feedId = feed.makeFeedId(),
            item = FeedItemResponse(
                resourceId = feed.item.resourceId.code,
                componentId = feed.item.componentId,
                payload = payload,
            )
        )
    }

}
