package com.story.platform.api.domain.feed

import com.story.platform.core.domain.event.EventAction

data class FeedMappingDisconnectApiRequest(
    val eventAction: EventAction,
)
