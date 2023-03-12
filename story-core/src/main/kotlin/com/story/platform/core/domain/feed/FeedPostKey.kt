package com.story.platform.core.domain.feed

import com.story.platform.core.domain.post.PostSpaceType
import com.story.platform.core.domain.subscription.SubscriptionType

data class FeedPostKey(
    val spaceType: PostSpaceType,
    val spaceId: String,
    val slotId: Long,
    val postId: Long,
)


data class FeedSubscriptionKey(
    val subscriptionType: SubscriptionType,
    val targetId: String,
    val subscriberId: String,
)
