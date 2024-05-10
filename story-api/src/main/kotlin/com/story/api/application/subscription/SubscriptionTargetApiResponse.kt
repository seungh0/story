package com.story.api.application.subscription

import com.story.core.domain.subscription.Subscription

data class SubscriptionTargetApiResponse(
    val targetId: String,
    val alarmEnabled: Boolean,
) {

    companion object {
        fun of(subscription: Subscription) = SubscriptionTargetApiResponse(
            targetId = subscription.targetId,
            alarmEnabled = subscription.alarmEnabled,
        )
    }

}
