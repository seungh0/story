package com.story.platform.api.domain.feed

import com.story.platform.core.domain.event.BaseEvent
import com.story.platform.core.domain.feed.FeedResponse

data class FeedApiResponse<T : BaseEvent>(
    val feedId: String,
    val resourceId: String,
    val componentId: String,
    val payload: T,
) {

    companion object {
        fun <T : BaseEvent> of(feed: FeedResponse<T>) = FeedApiResponse(
            feedId = feed.feedId.toString(),
            resourceId = feed.resourceId,
            componentId = feed.componentId,
            payload = feed.payload,
        )
    }

}
