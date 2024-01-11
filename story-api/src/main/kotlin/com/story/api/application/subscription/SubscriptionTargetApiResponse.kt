package com.story.api.application.subscription

import com.story.core.domain.subscription.SubscriptionResponse

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
