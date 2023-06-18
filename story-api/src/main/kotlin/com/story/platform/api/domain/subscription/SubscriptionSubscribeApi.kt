package com.story.platform.api.domain.subscription

import com.story.platform.api.domain.authentication.AuthenticationHandler
import com.story.platform.core.common.model.ApiResponse
import com.story.platform.core.domain.subscription.SubscriptionSubscribeHandler
import com.story.platform.core.domain.subscription.SubscriptionType
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange

@RestController
class SubscriptionSubscribeApi(
    private val subscriptionSubscribeHandler: SubscriptionSubscribeHandler,
    private val authenticationHandler: AuthenticationHandler,
) {

    /**
     * 구독을 등록한다
     */
    @PostMapping("/v1/subscriptions/{subscriptionType}/subscribers/{subscriberId}/targets/{targetId}")
    suspend fun subscribe(
        @PathVariable subscriptionType: SubscriptionType,
        @PathVariable subscriberId: String,
        @PathVariable targetId: String,
        @Valid @RequestBody request: SubscriptionSubscribeApiRequest,
        serverWebExchange: ServerWebExchange,
    ): ApiResponse<String> {
        val authentication = authenticationHandler.handleAuthentication(serverWebExchange = serverWebExchange)
        subscriptionSubscribeHandler.subscribe(
            serviceType = authentication.serviceType,
            subscriptionType = subscriptionType,
            targetId = targetId,
            subscriberId = subscriberId,
            alarm = request.alarm,
        )
        return ApiResponse.OK
    }

}
