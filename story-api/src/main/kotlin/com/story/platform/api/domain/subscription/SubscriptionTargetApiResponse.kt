package com.story.platform.api.domain.subscription

import com.story.platform.core.domain.subscription.SubscriptionResponse

data class SubscriptionTargetApiResponse(
    val targetId: String,
    val alarmEnabled: Boolean,
) {

    companion object {
        fun of(subscription: SubscriptionResponse) = SubscriptionTargetApiResponse(
            targetId = subscription.targetId,
            alarmEnabled = subscription.alarmEnabled,
        )
    }

}
