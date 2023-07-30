package com.story.platform.api.domain.subscription

import com.story.platform.api.config.auth.AuthContext
import com.story.platform.api.config.auth.RequestAuthContext
import com.story.platform.core.common.model.dto.ApiResponse
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
