package com.story.platform.api.domain.subscription

import com.story.platform.api.config.auth.AuthContext
import com.story.platform.api.config.auth.RequestAuthContext
import com.story.platform.core.common.model.dto.ApiResponse
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/v1/subscriptions/components/{componentId}")
@RestController
class SubscriptionUnsubscribeApi(
    private val subscriptionUnSubscribeHandler: SubscriptionUnSubscribeHandler,
) {

    /**
     * 구독을 취소한다
     */
    @DeleteMapping("/subscribers/{subscriberId}/targets/{targetId}")
    suspend fun unsubscribe(
        @PathVariable componentId: String,
        @PathVariable subscriberId: String,
        @PathVariable targetId: String,
        @RequestAuthContext authContext: AuthContext,
    ): ApiResponse<Nothing?> {
        subscriptionUnSubscribeHandler.unsubscribe(
            workspaceId = authContext.workspaceId,
            componentId = componentId,
            targetId = targetId,
            subscriberId = subscriberId,
        )
        return ApiResponse.OK
    }

}
