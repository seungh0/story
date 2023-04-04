package com.story.platform.api.domain.subscription

import com.story.platform.core.domain.subscription.Subscription

data class SubscriptionTargetResponse(
    val targetId: String,
) {

    companion object {
        fun of(subscription: Subscription) = SubscriptionTargetResponse(
            targetId = subscription.key.targetId,
        )
    }

}
