package com.story.api.application.subscription

import com.story.core.domain.feed.FeedPayload

data class SubscriptionResponse(
    val subscriberId: String,
    val targetId: String,
) : FeedPayload
