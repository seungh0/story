package com.story.api.application.feed

import com.story.core.domain.feed.FeedPayload
import com.story.core.domain.feed.FeedResponse

data class FeedApiResponse(
    val feedId: String,
    val resourceId: String,
    val componentId: String,
    val payload: FeedPayload,
) {

    companion object {
        fun of(feed: FeedResponse, payload: FeedPayload) = FeedApiResponse(
            feedId = feed.feedId.toString(),
            resourceId = feed.sourceResourceId.code,
            componentId = feed.sourceComponentId,
            payload = payload,
        )
    }

}
