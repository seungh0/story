package com.story.platform.api.domain.subscription

import com.story.platform.api.config.auth.AuthContext
import com.story.platform.api.config.auth.RequestAuthContext
import com.story.platform.api.domain.component.ComponentHandler
import com.story.platform.core.common.model.dto.ApiResponse
import com.story.platform.core.domain.resource.ResourceId
import com.story.platform.core.domain.subscription.SubscriptionUnSubscribeHandler
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/v1/subscriptions/components/{componentId}")
@RestController
class SubscriptionUnsubscribeApi(
    private val subscriptionUnSubscribeHandler: SubscriptionUnSubscribeHandler,
    private val componentHandler: ComponentHandler,
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
        componentHandler.validateComponent(
            workspaceId = authContext.workspaceId,
            resourceId = ResourceId.SUBSCRIPTIONS,
            componentId = componentId,
        )

        subscriptionUnSubscribeHandler.unsubscribe(
            workspaceId = authContext.workspaceId,
            componentId = componentId,
            targetId = targetId,
            subscriberId = subscriberId,
        )
        return ApiResponse.OK
    }

}
