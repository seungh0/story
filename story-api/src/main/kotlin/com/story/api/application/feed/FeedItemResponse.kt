package com.story.api.application.feed

import com.story.core.domain.feed.FeedPayload

data class FeedItemResponse(
    val resourceId: String,
    val componentId: String,
    val payload: FeedPayload,
)
