package com.story.api.application.subscription

import com.story.core.domain.feed.FeedPayload

data class SubscriptionApiResponse(
    val subscriberId: String,
    val targetId: String,
) : FeedPayload
