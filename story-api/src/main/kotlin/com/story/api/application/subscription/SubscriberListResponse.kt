package com.story.api.application.subscription

import com.story.core.common.model.Slice
import com.story.core.common.model.dto.CursorResponse
import com.story.core.common.model.dto.SlotRangeMarkerResponse
import com.story.core.common.model.dto.encode
import com.story.core.domain.subscription.Subscription

data class SubscriberListResponse(
    val subscribers: List<SubscriberResponse>,
    val cursor: CursorResponse<String>,
) {

    companion object {
        fun of(subscribers: Slice<Subscription, String>) = SubscriberListResponse(
            subscribers = subscribers.data.map { subscription -> SubscriberResponse.of(subscription = subscription) },
            cursor = subscribers.cursor.encode(),
        )

        fun of(subscribers: SlotRangeMarkerResponse<List<Subscription>>) = SubscriberListResponse(
            subscribers = subscribers.data.map { subscriber -> SubscriberResponse.of(subscriber) },
            cursor = CursorResponse.of(subscribers.nextMarker?.makeCursor()),
        )
    }

}
