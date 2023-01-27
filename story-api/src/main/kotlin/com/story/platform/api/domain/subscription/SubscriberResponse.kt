package com.story.platform.api.domain.subscription

import com.story.platform.core.domain.subscription.Subscription

data class SubscriberResponse(
    val subscriberId: String,
) {

    companion object {
        fun of(subscription: Subscription) = SubscriberResponse(
            subscriberId = subscription.key.subscriberId,
        )
    }

}
