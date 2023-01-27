package com.story.platform.api.domain.subscription

import com.story.platform.core.domain.subscription.SubscriptionReverse

data class SubscriptionTargetResponse(
    val targetId: String,
) {

    companion object {
        fun of(subscriptionReverse: SubscriptionReverse) = SubscriptionTargetResponse(
            targetId = subscriptionReverse.key.targetId,
        )
    }

}
