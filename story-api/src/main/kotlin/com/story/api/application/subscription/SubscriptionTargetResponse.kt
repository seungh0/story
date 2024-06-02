package com.story.api.application.subscription

import com.story.core.domain.subscription.Subscription

data class SubscriptionTargetResponse(
    val targetId: String,
    val alarmEnabled: Boolean,
) {

    companion object {
        fun of(subscription: Subscription) = SubscriptionTargetResponse(
            targetId = subscription.targetId,
            alarmEnabled = subscription.alarmEnabled,
        )
    }

}
