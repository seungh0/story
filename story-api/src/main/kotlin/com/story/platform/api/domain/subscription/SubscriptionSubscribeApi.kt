package com.story.platform.api.domain.subscription

import com.story.platform.api.config.AuthContext
import com.story.platform.api.config.RequestAuthContext
import com.story.platform.core.common.model.ApiResponse
import com.story.platform.core.domain.subscription.SubscriptionSubscribeHandler
import com.story.platform.core.domain.subscription.SubscriptionType
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class SubscriptionSubscribeApi(
    private val subscriptionSubscribeHandler: SubscriptionSubscribeHandler,
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
        @RequestAuthContext authContext: AuthContext,
    ): ApiResponse<String> {
        subscriptionSubscribeHandler.subscribe(
            serviceType = authContext.serviceType,
            subscriptionType = subscriptionType,
            targetId = targetId,
            subscriberId = subscriberId,
            alarm = request.alarm,
        )
        return ApiResponse.OK
    }

}
