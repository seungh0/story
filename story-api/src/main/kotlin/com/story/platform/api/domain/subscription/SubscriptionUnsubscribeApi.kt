package com.story.platform.api.domain.subscription

import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.common.model.ApiResponse
import com.story.platform.core.domain.subscription.SubscriptionType
import com.story.platform.core.domain.subscription.SubscriptionUnSubscriber
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class SubscriptionUnsubscribeApi(
    private val subscriptionUnSubscriber: SubscriptionUnSubscriber,
) {

    /**
     * 구독을 취소한다
     */
    @DeleteMapping("/v1/subscriptions/{subscriptionType}/subscribers/{subscriberId}/targets/{targetId}")
    suspend fun unsubscribe(
        @PathVariable subscriptionType: SubscriptionType,
        @PathVariable subscriberId: String,
        @PathVariable targetId: String,
    ): ApiResponse<String> {
        subscriptionUnSubscriber.unsubscribe(
            serviceType = ServiceType.TWEETER,
            subscriptionType = subscriptionType,
            targetId = targetId,
            subscriberId = subscriberId,
        )
        return ApiResponse.OK
    }

}
