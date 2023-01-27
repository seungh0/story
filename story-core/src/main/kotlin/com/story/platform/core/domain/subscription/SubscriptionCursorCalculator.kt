package com.story.platform.core.domain.subscription

import org.springframework.data.domain.Slice

object SubscriptionCursorCalculator {

    fun getNextCursorBySubscriptionReverse(subscriptionReverseSlice: Slice<SubscriptionReverse>): String? {
        return if (subscriptionReverseSlice.hasNext()) {
            subscriptionReverseSlice.last().key.subscriberId
        } else {
            null
        }
    }

    fun getNextCursorBySubscription(subscriptionSlice: Slice<Subscription>): String? {
        return if (subscriptionSlice.hasNext()) {
            subscriptionSlice.last().key.subscriberId
        } else {
            null
        }
    }

}
