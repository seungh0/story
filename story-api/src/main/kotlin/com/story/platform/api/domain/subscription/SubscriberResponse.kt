package com.story.platform.api.domain.subscription

import com.story.platform.core.domain.subscription.Subscriber

data class SubscriberResponse(
    val subscriberId: String,
) {

    companion object {
        fun of(subscriber: Subscriber) = SubscriberResponse(
            subscriberId = subscriber.key.subscriberId,
        )
    }

}
