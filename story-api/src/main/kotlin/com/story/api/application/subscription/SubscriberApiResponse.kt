package com.story.api.application.subscription

import com.story.core.domain.subscription.Subscription

data class SubscriberApiResponse(
    val subscriberId: String,
) {

    companion object {
        fun of(subscription: Subscription) = SubscriberApiResponse(
            subscriberId = subscription.subscriberId,
        )
    }

}
