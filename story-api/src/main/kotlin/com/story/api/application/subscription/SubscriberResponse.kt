package com.story.api.application.subscription

import com.story.core.domain.subscription.Subscription

data class SubscriberResponse(
    val subscriberId: String,
) {

    companion object {
        fun of(subscription: Subscription) = SubscriberResponse(
            subscriberId = subscription.subscriberId,
        )
    }

}
