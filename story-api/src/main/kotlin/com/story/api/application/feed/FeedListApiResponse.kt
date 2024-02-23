package com.story.api.application.feed

import com.story.core.common.model.Slice
import com.story.core.common.model.dto.CursorResponse
import com.story.core.common.model.dto.encode
import com.story.core.domain.feed.FeedPayload
import com.story.core.domain.feed.FeedResponse

data class FeedListApiResponse(
    val feeds: List<FeedApiResponse>,
    val cursor: CursorResponse<String>,
) {

    companion object {
        fun of(feeds: Slice<FeedResponse, String>, feedPayloads: Map<Long, FeedPayload>) = FeedListApiResponse(
            feeds = feeds.data.mapNotNull { feed ->
                return@mapNotNull feedPayloads[feed.feedId]?.let { payload ->
                    FeedApiResponse.of(
                        feed = feed,
                        payload = payload
                    )
                }
            },
            cursor = feeds.cursor.encode(),
        )
    }

}
