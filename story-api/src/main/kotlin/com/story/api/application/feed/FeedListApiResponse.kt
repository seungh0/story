package com.story.api.application.feed

import com.story.core.common.model.Slice
import com.story.core.common.model.dto.CursorResponse
import com.story.core.domain.event.BaseEvent
import com.story.core.domain.feed.FeedResponse

data class FeedListApiResponse(
    val feeds: List<FeedApiResponse<out BaseEvent>>,
    val cursor: CursorResponse<String>,
) {

    companion object {
        fun of(feeds: Slice<FeedResponse<out BaseEvent>, String>) = FeedListApiResponse(
            feeds = feeds.data.map { feed -> FeedApiResponse.of(feed = feed) },
            cursor = feeds.cursor,
        )
    }

}
