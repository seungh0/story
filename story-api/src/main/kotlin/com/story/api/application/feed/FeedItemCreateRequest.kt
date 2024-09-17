package com.story.api.application.feed

data class FeedItemCreateRequest(
    val resourceId: String,
    val componentId: String,
    val channelId: String,
    val itemId: String,
)
