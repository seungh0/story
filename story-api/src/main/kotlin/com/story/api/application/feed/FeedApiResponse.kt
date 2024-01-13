package com.story.api.application.feed

import com.story.core.domain.feed.FeedResponse

data class FeedApiResponse(
    val feedId: String,
    val resourceId: String,
    val componentId: String,
    val payload: Any? = null,
) {

    companion object {
        fun of(feed: FeedResponse) = FeedApiResponse(
            feedId = feed.feedId.toString(),
            resourceId = feed.resourceId,
            componentId = feed.componentId,
        )
    }

}
