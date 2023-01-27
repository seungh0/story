package com.story.platform.core.domain.subscription

data class SubscriptionResponse(
    val subscriberId: String,
) {

    companion object {
        fun of(subscription: Subscription) = SubscriptionResponse(
            subscriberId = subscription.key.subscriberId,
        )

        fun of(subscriptionReverse: SubscriptionReverse) = SubscriptionResponse(
            subscriberId = subscriptionReverse.key.subscriberId,
        )
    }

}
