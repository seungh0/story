package com.story.api.application.feed

import com.story.core.common.model.Slice
import com.story.core.common.model.dto.CursorResponse
import com.story.core.common.model.dto.encode
import com.story.core.domain.feed.Feed
import com.story.core.domain.feed.FeedPayload

data class FeedListResponse(
    val feeds: List<FeedResponse>,
    val cursor: CursorResponse<String>,
) {

    companion object {
        fun of(feeds: Slice<Feed, String>, feedPayloads: Map<Long, FeedPayload>) = FeedListResponse(
            feeds = feeds.data.mapNotNull { feed ->
                return@mapNotNull feedPayloads[feed.feedId]?.let { payload ->
                    FeedResponse.of(
                        feed = feed,
                        payload = payload
                    )
                }
            },
            cursor = feeds.cursor.encode(),
        )
    }

}
