package com.story.platform.api.domain.subscription

import com.story.platform.api.config.AuthContext
import com.story.platform.api.config.RequestAuthContext
import com.story.platform.core.common.model.ApiResponse
import com.story.platform.core.domain.subscription.SubscriptionType
import com.story.platform.core.domain.subscription.SubscriptionUnSubscribeHandler
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class SubscriptionUnsubscribeApi(
    private val subscriptionUnSubscribeHandler: SubscriptionUnSubscribeHandler,
) {

    /**
     * 구독을 취소한다
     */
    @DeleteMapping("/v1/subscriptions/{subscriptionType}/subscribers/{subscriberId}/targets/{targetId}")
    suspend fun unsubscribe(
        @PathVariable subscriptionType: SubscriptionType,
        @PathVariable subscriberId: String,
        @PathVariable targetId: String,
        @RequestAuthContext authContext: AuthContext,
    ): ApiResponse<String> {
        subscriptionUnSubscribeHandler.unsubscribe(
            serviceType = authContext.serviceType,
            subscriptionType = subscriptionType,
            targetId = targetId,
            subscriberId = subscriberId,
        )
        return ApiResponse.OK
    }

}
