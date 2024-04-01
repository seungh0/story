package com.story.api.application.subscription

import com.story.api.config.apikey.ApiKeyContext
import com.story.api.config.apikey.RequestApiKey
import com.story.core.common.model.dto.ApiResponse
import com.story.core.common.model.dto.SlotRangeMarkerResponse
import com.story.core.domain.subscription.SubscriptionResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class SubscriptionDistributedRetrieveApi(
    private val subscriptionDistributedRetrieveHandler: SubscriptionDistributedRetrieveHandler,
) {

    /**
     * 구독자 목록을 분산 조회한다 (마커 조회)
     */
    @GetMapping("/v1/resources/subscriptions/components/{componentId}/targets/{targetId}/subscribers/distributed-markers")
    suspend fun getSubscriberDistributedMarkers(
        @PathVariable componentId: String,
        @PathVariable targetId: String,
        @Valid request: SubscriberDistributedMarkerListRequest,
        @RequestApiKey authContext: ApiKeyContext,
    ): ApiResponse<SubscriberDistributedMarkerListResponse> {
        val response = subscriptionDistributedRetrieveHandler.getSubscriberDistributedMarkers(
            workspaceId = authContext.workspaceId,
            componentId = componentId,
            targetId = targetId,
            request = request,
        )
        return ApiResponse.ok(response)
    }

    /**
     * 구독자 목록을 분산 조회한다 (마커 기반의 실제 조회)
     */
    @GetMapping("/v1/resources/subscriptions/components/{componentId}/targets/{targetId}/subscribers/distributed")
    suspend fun listSubscribersByDistributedMarkers(
        @PathVariable componentId: String,
        @PathVariable targetId: String,
        @Valid request: SubscriberListApiRequest,
        @RequestApiKey authContext: ApiKeyContext,
    ): ApiResponse<SlotRangeMarkerResponse<List<SubscriptionResponse>>> {
        val response = subscriptionDistributedRetrieveHandler.listSubscribersByDistributedMarkers(
            workspaceId = authContext.workspaceId,
            componentId = componentId,
            targetId = targetId,
            request = request,
        )
        return ApiResponse.ok(response)
    }

}
