package com.story.platform.api.domain.subscription

import com.story.platform.core.common.model.Cursor
import com.story.platform.core.common.model.CursorResult
import com.story.platform.core.domain.subscription.SubscriptionResponse

data class SubscriberListApiResponse(
    val subscribers: List<SubscriberApiResponse>,
    val cursor: Cursor<String>,
) {

    companion object {
        fun of(subscriptions: CursorResult<SubscriptionResponse, String>) = SubscriberListApiResponse(
            subscribers = subscriptions.data.map { subscription -> SubscriberApiResponse.of(subscription = subscription) },
            cursor = subscriptions.cursor,
        )
    }

}
