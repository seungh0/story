package com.story.platform.api.domain.subscription

import com.story.platform.api.domain.authentication.AuthenticationHandler
import com.story.platform.core.common.model.ApiResponse
import com.story.platform.core.domain.subscription.SubscriptionType
import com.story.platform.core.domain.subscription.SubscriptionUnSubscribeHandler
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange

@RestController
class SubscriptionUnsubscribeApi(
    private val subscriptionUnSubscribeHandler: SubscriptionUnSubscribeHandler,
    private val authenticationHandler: AuthenticationHandler,
) {

    /**
     * 구독을 취소한다
     */
    @DeleteMapping("/v1/subscriptions/{subscriptionType}/subscribers/{subscriberId}/targets/{targetId}")
    suspend fun unsubscribe(
        @PathVariable subscriptionType: SubscriptionType,
        @PathVariable subscriberId: String,
        @PathVariable targetId: String,
        serverWebExchange: ServerWebExchange,
    ): ApiResponse<String> {
        val authentication = authenticationHandler.handleAuthentication(serverWebExchange = serverWebExchange)
        subscriptionUnSubscribeHandler.unsubscribe(
            serviceType = authentication.serviceType,
            subscriptionType = subscriptionType,
            targetId = targetId,
            subscriberId = subscriberId,
        )
        return ApiResponse.OK
    }

}
