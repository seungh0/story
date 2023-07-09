package com.story.platform.api.domain.subscription

import com.story.platform.api.config.auth.AuthContext
import com.story.platform.api.config.auth.RequestAuthContext
import com.story.platform.api.domain.component.ComponentHandler
import com.story.platform.core.common.model.ApiResponse
import com.story.platform.core.common.model.CursorRequest
import com.story.platform.core.common.model.CursorResult
import com.story.platform.core.domain.resource.ResourceId
import com.story.platform.core.domain.subscription.SubscriptionCountRetriever
import com.story.platform.core.domain.subscription.SubscriptionRetriever
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/v1/subscriptions/components/{componentId}")
@RestController
class SubscriptionRetrieveApi(
    private val subscriptionRetriever: SubscriptionRetriever,
    private val subscriptionCountRetriever: SubscriptionCountRetriever,
    private val componentHandler: ComponentHandler,
) {

    /**
     * 대상자의 구독자인지 확인한다
     */
    @GetMapping("/subscribers/{subscriberId}/targets/{targetId}")
    suspend fun isSubscriber(
        @PathVariable componentId: String,
        @PathVariable subscriberId: String,
        @PathVariable targetId: String,
        @RequestAuthContext authContext: AuthContext,
    ): ApiResponse<SubscriptionCheckApiResponse> {
        componentHandler.validateComponent(
            workspaceId = authContext.workspaceId,
            resourceId = ResourceId.SUBSCRIPTIONS,
            componentId = componentId,
        )

        val isSubscriber = subscriptionRetriever.isSubscriber(
            workspaceId = authContext.workspaceId,
            componentId = componentId,
            targetId = targetId,
            subscriberId = subscriberId,
        )
        return ApiResponse.ok(
            result = SubscriptionCheckApiResponse(isSubscriber = isSubscriber)
        )
    }

    /**
     * 구독자 수를 조회한다
     */
    @GetMapping("/targets/{targetId}/subscribers/count")
    suspend fun countSubscribers(
        @PathVariable componentId: String,
        @PathVariable targetId: String,
        @RequestAuthContext authContext: AuthContext,
    ): ApiResponse<SubscribersCountApiResponse> {
        componentHandler.validateComponent(
            workspaceId = authContext.workspaceId,
            resourceId = ResourceId.SUBSCRIPTIONS,
            componentId = componentId,
        )

        val subscribersCount = subscriptionCountRetriever.countSubscribers(
            workspaceId = authContext.workspaceId,
            componentId = componentId,
            targetId = targetId,
        )
        return ApiResponse.ok(
            result = SubscribersCountApiResponse(subscribersCount = subscribersCount)
        )
    }

    /**
     * 구독 대상자 수를 조회한다
     */
    @GetMapping("/subscribers/{subscriberId}/subscriptions/count")
    suspend fun countSubscriptions(
        @PathVariable componentId: String,
        @PathVariable subscriberId: String,
        @RequestAuthContext authContext: AuthContext,
    ): ApiResponse<SubscriptionsCountApiResponse> {
        componentHandler.validateComponent(
            workspaceId = authContext.workspaceId,
            resourceId = ResourceId.SUBSCRIPTIONS,
            componentId = componentId,
        )

        val subscribersCount = subscriptionCountRetriever.countSubscriptions(
            workspaceId = authContext.workspaceId,
            componentId = componentId,
            subscriberId = subscriberId,
        )
        return ApiResponse.ok(
            result = SubscriptionsCountApiResponse(subscriptionsCount = subscribersCount)
        )
    }

    /**
     * 구독자 목록을 조회한다
     */
    @GetMapping("/targets/{targetId}/subscribers")
    suspend fun listTargetSubscribers(
        @PathVariable componentId: String,
        @PathVariable targetId: String,
        @Valid cursorRequest: CursorRequest,
        @RequestAuthContext authContext: AuthContext,
    ): ApiResponse<CursorResult<SubscriberApiResponse, String>> {
        componentHandler.validateComponent(
            workspaceId = authContext.workspaceId,
            resourceId = ResourceId.SUBSCRIPTIONS,
            componentId = componentId,
        )

        val subscriptionReverses = subscriptionRetriever.listTargetSubscribers(
            workspaceId = authContext.workspaceId,
            componentId = componentId,
            targetId = targetId,
            cursorRequest = cursorRequest,
        )
        return ApiResponse.ok(
            result = CursorResult.of(
                data = subscriptionReverses.data.map { subscriptionReverse ->
                    SubscriberApiResponse.of(subscriptionReverse)
                },
                cursor = subscriptionReverses.cursor,
            )
        )
    }

    /**
     * 구독중인 대상자 목록을 조회한다
     */
    @GetMapping("/subscribers/{subscriberId}/targets")
    suspend fun listSubscriberTargets(
        @PathVariable componentId: String,
        @PathVariable subscriberId: String,
        @Valid cursorRequest: CursorRequest,
        @RequestAuthContext authContext: AuthContext,
    ): ApiResponse<CursorResult<SubscriptionTargetApiResponse, String>> {
        componentHandler.validateComponent(
            workspaceId = authContext.workspaceId,
            resourceId = ResourceId.SUBSCRIPTIONS,
            componentId = componentId,
        )

        val subscriptions = subscriptionRetriever.listSubscriberTargets(
            workspaceId = authContext.workspaceId,
            componentId = componentId,
            subscriberId = subscriberId,
            cursorRequest = cursorRequest,
        )
        return ApiResponse.ok(
            result = CursorResult.of(
                data = subscriptions.data.map { subscription ->
                    SubscriptionTargetApiResponse.of(subscription)
                },
                cursor = subscriptions.cursor,
            )
        )
    }

}
