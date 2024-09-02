package com.story.core.domain.feed

data class FeedFanoutMessage(
    val workspaceId: String,
    val componentId: String,
    val subscriptionComponentId: String,
    val targetId: String,
    val item: FeedItem,
    val slotId: Long,
    val options: FeedOptions,
)
