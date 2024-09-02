package com.story.api.application.feed

import com.story.core.domain.feed.Feed
import com.story.core.domain.feed.FeedPayload

data class FeedResponse(
    val feedId: String,
    val itemResourceId: String,
    val itemComponentId: String,
    val item: FeedPayload,
) {

    companion object {
        fun of(feed: Feed, payload: FeedPayload) = FeedResponse(
            feedId = feed.makeFeedId(),
            itemResourceId = feed.item.resourceId.code,
            itemComponentId = feed.item.componentId,
            item = payload,
        )
    }

}
