package com.story.platform.api.application.subscription

import com.story.platform.api.config.auth.AuthContext
import com.story.platform.api.config.auth.RequestAuthContext
import com.story.platform.core.common.model.dto.ApiResponse
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class SubscriptionRemoveApi(
    private val subscriptionRemoveHandler: SubscriptionRemoveHandler,
) {

    /**
     * 구독을 취소한다
     */
    @DeleteMapping("/v1/resources/subscriptions/components/{componentId}/subscribers/{subscriberId}/targets/{targetId}")
    suspend fun removeSubscription(
        @PathVariable componentId: String,
        @PathVariable subscriberId: String,
        @PathVariable targetId: String,
        @RequestAuthContext authContext: AuthContext,
    ): ApiResponse<Nothing?> {
        subscriptionRemoveHandler.removeSubscription(
            workspaceId = authContext.workspaceId,
            componentId = componentId,
            targetId = targetId,
            subscriberId = subscriberId,
        )
        return ApiResponse.OK
    }

}
