package com.story.core.domain.subscription

data class Subscription(
    val targetId: String,
    val subscriberId: String,
    val alarmEnabled: Boolean,
) {

    companion object {
        fun of(
            subscriber: SubscriberEntity,
        ) = Subscription(
            targetId = subscriber.key.targetId,
            subscriberId = subscriber.key.subscriberId,
            alarmEnabled = subscriber.alarmEnabled,
        )

        fun of(
            subscription: SubscriptionEntity,
        ) = Subscription(
            targetId = subscription.key.targetId,
            subscriberId = subscription.key.subscriberId,
            alarmEnabled = subscription.alarmEnabled,
        )
    }

}
