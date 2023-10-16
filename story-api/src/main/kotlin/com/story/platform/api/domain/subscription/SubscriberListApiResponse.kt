package com.story.platform.api.domain.subscription

import com.story.platform.core.common.model.Slice
import com.story.platform.core.common.model.dto.CursorResponse
import com.story.platform.core.domain.subscription.SubscriptionResponse

data class SubscriberListApiResponse(
    val subscribers: List<SubscriberApiResponse>,
    val cursor: CursorResponse<String>,
) {

    companion object {
        fun of(subscriptions: Slice<SubscriptionResponse, String>) = SubscriberListApiResponse(
            subscribers = subscriptions.data.map { subscription -> SubscriberApiResponse.of(subscription = subscription) },
            cursor = subscriptions.cursor,
        )
    }

}
