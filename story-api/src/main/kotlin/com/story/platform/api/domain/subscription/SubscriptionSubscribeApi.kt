package com.story.platform.api.domain.subscription

import com.story.platform.api.config.auth.AuthContext
import com.story.platform.api.config.auth.RequestAuthContext
import com.story.platform.api.domain.component.ComponentHandler
import com.story.platform.core.common.model.ApiResponse
import com.story.platform.core.domain.resource.ResourceId
import com.story.platform.core.domain.subscription.SubscriptionSubscribeHandler
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/v1/subscriptions/components/{componentId}")
@RestController
class SubscriptionSubscribeApi(
    private val subscriptionSubscribeHandler: SubscriptionSubscribeHandler,
    private val componentHandler: ComponentHandler,
) {

    /**
     * 구독을 등록한다
     */
    @PostMapping("/subscribers/{subscriberId}/targets/{targetId}")
    suspend fun subscribe(
        @PathVariable componentId: String,
        @PathVariable subscriberId: String,
        @PathVariable targetId: String,
        @Valid @RequestBody request: SubscriptionSubscribeApiRequest,
        @RequestAuthContext authContext: AuthContext,
    ): ApiResponse<Nothing?> {
        componentHandler.validateComponent(
            workspaceId = authContext.workspaceId,
            resourceId = ResourceId.SUBSCRIPTIONS,
            componentId = componentId,
        )

        subscriptionSubscribeHandler.subscribe(
            workspaceId = authContext.workspaceId,
            componentId = componentId,
            targetId = targetId,
            subscriberId = subscriberId,
            alarm = request.alarm,
        )
        return ApiResponse.OK
    }

}
