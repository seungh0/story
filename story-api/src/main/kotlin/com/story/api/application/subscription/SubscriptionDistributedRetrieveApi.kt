package com.story.api.application.subscription

import com.story.api.config.apikey.ApiKeyContext
import com.story.api.config.apikey.RequestApiKey
import com.story.core.common.model.dto.ApiResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class SubscriptionDistributedRetrieveApi(
    private val subscriptionDistributedRetrieveHandler: SubscriptionDistributedRetrieveHandler,
) {

    /**
     * 구독자 목록을 분산 조회한다 (분산 조회를 위한 시작 마커 목록 조회)
     */
    @GetMapping("/v1/resources/subscriptions/components/{componentId}/targets/{targetId}/subscribers/distributed-cursors")
    suspend fun getSubscriberDistributedMarkers(
        @PathVariable componentId: String,
        @PathVariable targetId: String,
        @Valid request: SubscriberDistributedMarkerListRequest,
        @RequestApiKey authContext: ApiKeyContext,
    ): ApiResponse<SubscriberDistributedMarkerListResponse> {
        val response = subscriptionDistributedRetrieveHandler.listSubscriberDistributedMarkers(
            workspaceId = authContext.workspaceId,
            componentId = componentId,
            targetId = targetId,
            request = request,
        )
        return ApiResponse.ok(response)
    }

    /**
     * 구독자 목록을 분산 조회한다 (마커 기반의 구독자 조회)
     */
    @GetMapping("/v1/resources/subscriptions/components/{componentId}/targets/{targetId}/subscribers/distributed")
    suspend fun listSubscribersByDistributedMarkers(
        @PathVariable componentId: String,
        @PathVariable targetId: String,
        @Valid request: SubscriberListApiRequest,
        @RequestApiKey authContext: ApiKeyContext,
    ): ApiResponse<SubscriberListApiResponse> {
        val response = subscriptionDistributedRetrieveHandler.listSubscribersByDistributedMarkers(
            workspaceId = authContext.workspaceId,
            componentId = componentId,
            targetId = targetId,
            request = request,
        )
        return ApiResponse.ok(response)
    }

}
