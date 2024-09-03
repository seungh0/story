package com.story.core.domain.feed

data class FeedFanoutMessage(
    val slotId: Long,
    val workspaceId: String,
    val componentId: String,
    val subscriptionComponentId: String,
    val targetId: String,
    val item: FeedItem,
    val priority: Long,
    val options: FeedOptions,
)
