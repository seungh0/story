package com.story.platform.core.domain.subscription

import org.springframework.data.domain.Slice

object SubscriberCursorCalculator {

    fun getNextCursorBySubscription(subscriberSlice: Slice<Subscriber>): String? {
        return if (subscriberSlice.hasNext()) {
            subscriberSlice.last().key.subscriberId
        } else {
            null
        }
    }

}
