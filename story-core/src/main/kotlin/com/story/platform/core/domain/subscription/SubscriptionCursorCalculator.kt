package com.story.platform.core.domain.subscription

import org.springframework.data.domain.Slice

object SubscriptionCursorCalculator {

    fun getNextCursorBySubscription(subscriptionSlice: Slice<Subscription>): String? {
        return if (subscriptionSlice.hasNext()) {
            subscriptionSlice.last().key.subscriberId
        } else {
            null
        }
    }

}
