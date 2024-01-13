package com.story.api.application.feed

import com.story.core.common.model.Slice
import com.story.core.common.model.dto.CursorResponse
import com.story.core.domain.feed.FeedResponse

data class FeedListApiResponse(
    val feeds: List<FeedApiResponse>,
    val cursor: CursorResponse<String>,
    val payload: String? = null, // TODO: 컴포넌트 별로 반환 모델 처리
) {

    companion object {
        fun of(feeds: Slice<FeedResponse, String>) = FeedListApiResponse(
            feeds = feeds.data.map { feed -> FeedApiResponse.of(feed = feed) },
            cursor = feeds.cursor,
        )
    }

}
