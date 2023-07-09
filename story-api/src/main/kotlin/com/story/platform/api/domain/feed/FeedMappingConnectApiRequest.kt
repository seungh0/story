package com.story.platform.api.domain.feed

import com.story.platform.core.domain.event.EventAction

data class FeedMappingConnectApiRequest(
    val eventAction: EventAction,
    val description: String,
)
