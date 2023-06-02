package com.story.platform.api.domain.subscription

import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.common.model.ApiResponse
import com.story.platform.core.common.model.CursorRequest
import com.story.platform.core.common.model.CursorResult
import com.story.platform.core.domain.subscription.SubscriptionCountRetriever
import com.story.platform.core.domain.subscription.SubscriptionRetriever
import com.story.platform.core.domain.subscription.SubscriptionType
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/v1/subscriptions/{subscriptionType}")
@RestController
class SubscriptionRetrieveApi(
    private val subscriptionRetriever: SubscriptionRetriever,
    private val subscriptionCountRetriever: SubscriptionCountRetriever,
) {

    /**
     * 대상자의 구독자인지 확인한다
     */
    @GetMapping("/subscribers/{subscriberId}/targets/{targetId}/exists")
    suspend fun isSubscriber(
        @PathVariable subscriptionType: SubscriptionType,
        @PathVariable subscriberId: String,
        @PathVariable targetId: String,
    ): ApiResponse<SubscriptionCheckApiResponse> {
        val isSubscriber = subscriptionRetriever.isSubscriber(
            serviceType = ServiceType.TWEETER,
            subscriptionType = subscriptionType,
            targetId = targetId,
            subscriberId = subscriberId,
        )
        return ApiResponse.success(
            result = SubscriptionCheckApiResponse(isSubscriber = isSubscriber)
        )
    }

    /**
     * 구독자 수를 조회한다
     */
    @GetMapping("/targets/{targetId}/subscribers/count")
    suspend fun countSubscribers(
        @PathVariable subscriptionType: SubscriptionType,
        @PathVariable targetId: String,
    ): ApiResponse<SubscribersCountApiResponse> {
        val subscribersCount = subscriptionCountRetriever.countSubscribers(
            serviceType = ServiceType.TWEETER,
            subscriptionType = subscriptionType,
            targetId = targetId,
        )
        return ApiResponse.success(
            result = SubscribersCountApiResponse(subscribersCount = subscribersCount)
        )
    }

    /**
     * 구독 대상자 수를 조회한다
     */
    @GetMapping("/subscribers/{subscriberId}/subscriptions/count")
    suspend fun countSubscriptions(
        @PathVariable subscriptionType: SubscriptionType,
        @PathVariable subscriberId: String,
    ): ApiResponse<SubscriptionsCountApiResponse> {
        val subscribersCount = subscriptionCountRetriever.countSubscriptions(
            serviceType = ServiceType.TWEETER,
            subscriptionType = subscriptionType,
            subscriberId = subscriberId,
        )
        return ApiResponse.success(
            result = SubscriptionsCountApiResponse(subscriptionsCount = subscribersCount)
        )
    }

    /**
     * 구독자 목록을 조회한다
     */
    @GetMapping("/targets/{targetId}/subscribers")
    suspend fun listTargetSubscribers(
        @PathVariable subscriptionType: SubscriptionType,
        @PathVariable targetId: String,
        @Valid cursorRequest: CursorRequest,
    ): ApiResponse<CursorResult<SubscriberApiResponse, String>> {
        val subscriptionReverses = subscriptionRetriever.listTargetSubscribers(
            serviceType = ServiceType.TWEETER,
            subscriptionType = subscriptionType,
            targetId = targetId,
            cursorRequest = cursorRequest,
        )
        return ApiResponse.success(
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
        @PathVariable subscriptionType: SubscriptionType,
        @PathVariable subscriberId: String,
        @Valid cursorRequest: CursorRequest,
    ): ApiResponse<CursorResult<SubscriptionTargetApiResponse, String>> {
        val subscriptions = subscriptionRetriever.listSubscriberTargets(
            serviceType = ServiceType.TWEETER,
            subscriptionType = subscriptionType,
            subscriberId = subscriberId,
            cursorRequest = cursorRequest,
        )
        return ApiResponse.success(
            result = CursorResult.of(
                data = subscriptions.data.map { subscription ->
                    SubscriptionTargetApiResponse.of(subscription)
                },
                cursor = subscriptions.cursor,
            )
        )
    }

}
