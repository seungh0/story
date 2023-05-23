package com.story.platform.api.domain.subscription

import com.story.platform.core.domain.subscription.Subscriber

data class SubscriberApiResponse(
    val subscriberId: String,
) {

    companion object {
        fun of(subscriber: Subscriber) = SubscriberApiResponse(
            subscriberId = subscriber.key.subscriberId,
        )
    }

}
