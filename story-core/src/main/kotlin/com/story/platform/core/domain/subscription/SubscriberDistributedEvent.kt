package com.story.platform.core.domain.subscription

data class SubscriberDistributedEvent(
    val workspaceId: String,
    val componentId: String,
    val targetId: String,
    val slot: Long,
    // TODO: 피드를 생성할때 필요한 정보들...
)
