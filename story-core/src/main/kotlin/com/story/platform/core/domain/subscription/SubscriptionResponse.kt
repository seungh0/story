package com.story.platform.core.domain.subscription

data class SubscriptionResponse(
    val targetId: String,
    val subscriberId: String,
    val alarm: Boolean,
) {

    companion object {
        fun of(
            subscriber: Subscriber,
        ) = SubscriptionResponse(
            targetId = subscriber.key.targetId,
            subscriberId = subscriber.key.subscriberId,
            alarm = subscriber.alarm,
        )

        fun of(
            subscription: Subscription,
        ) = SubscriptionResponse(
            targetId = subscription.key.targetId,
            subscriberId = subscription.key.subscriberId,
            alarm = subscription.alarm,
        )
    }

}
