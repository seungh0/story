package com.story.platform.core.domain.feed

import com.story.platform.core.domain.subscription.SubscriptionType

data class FeedSubscriptionKey(
    val subscriptionType: SubscriptionType,
    val targetId: String,
    val subscriberId: String,
)
