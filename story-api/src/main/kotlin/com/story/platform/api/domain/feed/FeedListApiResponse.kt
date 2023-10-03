package com.story.platform.api.domain.feed

import com.story.platform.core.common.model.ContentsWithCursor
import com.story.platform.core.common.model.Cursor
import com.story.platform.core.domain.event.BaseEvent
import com.story.platform.core.domain.feed.FeedResponse

data class FeedListApiResponse(
    val feeds: List<FeedApiResponse<out BaseEvent>>,
    val cursor: Cursor<String>,
) {

    companion object {
        fun of(feeds: ContentsWithCursor<FeedResponse<out BaseEvent>, String>) = FeedListApiResponse(
            feeds = feeds.data.map { feed -> FeedApiResponse.of(feed = feed) },
            cursor = feeds.cursor,
        )
    }

}
