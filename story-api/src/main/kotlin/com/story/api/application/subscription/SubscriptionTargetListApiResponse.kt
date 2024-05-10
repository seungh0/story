package com.story.api.application.subscription

import com.story.core.common.model.Slice
import com.story.core.common.model.dto.CursorResponse
import com.story.core.common.model.dto.encode
import com.story.core.domain.subscription.Subscription

data class SubscriptionTargetListApiResponse(
    val targets: List<SubscriptionTargetApiResponse>,
    val cursor: CursorResponse<String>,
) {

    companion object {
        fun of(subscriptions: Slice<Subscription, String>) = SubscriptionTargetListApiResponse(
            targets = subscriptions.data.map { subscription -> SubscriptionTargetApiResponse.of(subscription = subscription) },
            cursor = subscriptions.cursor.encode(),
        )
    }

}
