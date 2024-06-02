package com.story.api.application.subscription

import com.story.api.config.apikey.ApiKeyContext
import com.story.api.config.apikey.RequestApiKey
import com.story.core.common.model.dto.ApiResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class SubscriptionUpsertApi(
    private val subscriptionUpsertHandler: SubscriptionUpsertHandler,
) {

    /**
     * 구독을 등록한다
     */
    @PutMapping("/v1/resources/subscriptions/components/{componentId}/subscribers/{subscriberId}/targets/{targetId}")
    suspend fun upsertSubscription(
        @PathVariable componentId: String,
        @PathVariable subscriberId: String,
        @PathVariable targetId: String,
        @Valid @RequestBody request: SubscriptionUpsertRequest,
        @RequestApiKey authContext: ApiKeyContext,
    ): ApiResponse<Nothing?> {
        subscriptionUpsertHandler.upsertSubscription(
            workspaceId = authContext.workspaceId,
            componentId = componentId,
            targetId = targetId,
            subscriberId = subscriberId,
            alarm = request.alarmEnabled,
        )
        return ApiResponse.OK
    }

}
