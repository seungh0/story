package com.story.platform.api.application.subscription

import com.story.platform.api.config.auth.AuthContext
import com.story.platform.api.config.auth.RequestAuthContext
import com.story.platform.core.common.model.dto.ApiResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class SubscriptionRetrieveApi(
    private val subscriptionRetrieveHandler: SubscriptionRetrieveHandler,
) {

    /**
     * 대상자의 구독자인지 확인한다
     */
    @GetMapping("/v1/resources/subscriptions/components/{componentId}/subscribers/{subscriberId}/targets/{targetId}/exists")
    suspend fun existsSubscription(
        @PathVariable componentId: String,
        @PathVariable subscriberId: String,
        @PathVariable targetId: String,
        @RequestAuthContext authContext: AuthContext,
    ): ApiResponse<SubscriptionExistsApiResponse> {
        val response = subscriptionRetrieveHandler.existsSubscription(
            workspaceId = authContext.workspaceId,
            componentId = componentId,
            targetId = targetId,
            subscriberId = subscriberId,
        )
        return ApiResponse.ok(result = response)
    }

    /**
     * 구독자 수를 조회한다
     */
    @GetMapping("/v1/resources/subscriptions/components/{componentId}/targets/{targetId}/subscriber-count")
    suspend fun countSubscribers(
        @PathVariable componentId: String,
        @PathVariable targetId: String,
        @RequestAuthContext authContext: AuthContext,
    ): ApiResponse<SubscriberCountApiResponse> {
        val response = subscriptionRetrieveHandler.countSubscribers(
            workspaceId = authContext.workspaceId,
            componentId = componentId,
            targetId = targetId,
        )
        return ApiResponse.ok(result = response)
    }

    /**
     * 구독 대상자 수를 조회한다
     */
    @GetMapping("/v1/resources/subscriptions/components/{componentId}/subscribers/{subscriberId}/subscription-count")
    suspend fun countTargets(
        @PathVariable componentId: String,
        @PathVariable subscriberId: String,
        @RequestAuthContext authContext: AuthContext,
    ): ApiResponse<SubscriptionTargetCountApiResponse> {
        val response = subscriptionRetrieveHandler.countTargets(
            workspaceId = authContext.workspaceId,
            componentId = componentId,
            subscriberId = subscriberId,
        )
        return ApiResponse.ok(result = response)
    }

    /**
     * 구독자 목록을 조회한다
     */
    @GetMapping("/v1/resources/subscriptions/components/{componentId}/targets/{targetId}/subscribers")
    suspend fun listSubscribers(
        @PathVariable componentId: String,
        @PathVariable targetId: String,
        @Valid request: SubscriberListApiRequest,
        @RequestAuthContext authContext: AuthContext,
    ): ApiResponse<SubscriberListApiResponse> {
        val response = subscriptionRetrieveHandler.listSubscribers(
            workspaceId = authContext.workspaceId,
            componentId = componentId,
            targetId = targetId,
            request = request,
        )
        return ApiResponse.ok(response)
    }

    /**
     * 구독중인 대상자 목록을 조회한다
     */
    @GetMapping("/v1/resources/subscriptions/components/{componentId}/subscribers/{subscriberId}/targets")
    suspend fun listSubscriptionTargets(
        @PathVariable componentId: String,
        @PathVariable subscriberId: String,
        @Valid request: SubscriptionTargetListApiRequest,
        @RequestAuthContext authContext: AuthContext,
    ): ApiResponse<SubscriptionTargetListApiResponse> {
        val response = subscriptionRetrieveHandler.listSubscriptionTargets(
            workspaceId = authContext.workspaceId,
            componentId = componentId,
            subscriberId = subscriberId,
            request = request,
        )
        return ApiResponse.ok(response)
    }

}