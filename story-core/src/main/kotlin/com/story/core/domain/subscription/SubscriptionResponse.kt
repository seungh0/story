package com.story.core.domain.subscription

data class SubscriptionResponse(
    val targetId: String,
    val subscriberId: String,
    val alarmEnabled: Boolean,
) {

    companion object {
        fun of(
            subscriber: Subscriber,
        ) = SubscriptionResponse(
            targetId = subscriber.key.targetId,
            subscriberId = subscriber.key.subscriberId,
            alarmEnabled = subscriber.alarmEnabled,
        )

        fun of(
            subscription: Subscription,
        ) = SubscriptionResponse(
            targetId = subscription.key.targetId,
            subscriberId = subscription.key.subscriberId,
            alarmEnabled = subscription.alarmEnabled,
        )
    }

}
