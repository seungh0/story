package com.story.platform.api.domain.subscription

import com.story.platform.core.domain.subscription.Subscription

data class SubscriptionTargetApiResponse(
    val targetId: String,
    val alarm: Boolean,
) {

    companion object {
        fun of(subscription: Subscription) = SubscriptionTargetApiResponse(
            targetId = subscription.key.targetId,
            alarm = subscription.alarm,
        )
    }

}
