package com.story.platform.api.application.subscription

import com.story.platform.core.domain.subscription.SubscriptionResponse

data class SubscriberApiResponse(
    val subscriberId: String,
) {

    companion object {
        fun of(subscription: SubscriptionResponse) = SubscriberApiResponse(
            subscriberId = subscription.subscriberId,
        )
    }

}
