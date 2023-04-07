package com.story.platform.api.domain.subscription

import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.common.model.ApiResponse
import com.story.platform.core.common.model.CursorRequest
import com.story.platform.core.common.model.CursorResult
import com.story.platform.core.domain.subscription.SubscriptionRetriever
import com.story.platform.core.domain.subscription.SubscriptionType
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/v1/subscription/{subscriptionType}")
@RestController
class SubscriptionRetrieveApi(
    private val subscriptionRetriever: SubscriptionRetriever,
) {

    @GetMapping("/subscriber/{subscriberId}/target/{targetId}/exists")
    suspend fun isSubscriber(
        @PathVariable subscriptionType: SubscriptionType,
        @PathVariable subscriberId: String,
        @PathVariable targetId: String,
    ): ApiResponse<SubscriptionExistsResponse> {
        val exists = subscriptionRetriever.isSubscriber(
            serviceType = ServiceType.TWEETER,
            subscriptionType = subscriptionType,
            targetId = targetId,
            subscriberId = subscriberId,
        )
        return ApiResponse.success(
            result = SubscriptionExistsResponse(exists = exists)
        )
    }

    @GetMapping("/target/{targetId}/subscribers/count")
    suspend fun countSubscribers(
        @PathVariable subscriptionType: SubscriptionType,
        @PathVariable targetId: String,
    ): ApiResponse<SubscribersCountResponse> {
        val subscribersCount = subscriptionRetriever.countSubscribers(
            serviceType = ServiceType.TWEETER,
            subscriptionType = subscriptionType,
            targetId = targetId,
        )
        return ApiResponse.success(
            result = SubscribersCountResponse(count = subscribersCount)
        )
    }

    @GetMapping("/subscriber/{subscriberId}/subscriptions/count")
    suspend fun countSubscriptions(
        @PathVariable subscriptionType: SubscriptionType,
        @PathVariable subscriberId: String,
    ): ApiResponse<SubscriptionsCountResponse> {
        val subscribersCount = subscriptionRetriever.countSubscriptions(
            serviceType = ServiceType.TWEETER,
            subscriptionType = subscriptionType,
            subscriberId = subscriberId,
        )
        return ApiResponse.success(
            result = SubscriptionsCountResponse(count = subscribersCount)
        )
    }

    @GetMapping("/target/{targetId}/subscribers")
    suspend fun getSubscribers(
        @PathVariable subscriptionType: SubscriptionType,
        @PathVariable targetId: String,
        @Valid cursorRequest: CursorRequest,
    ): ApiResponse<CursorResult<SubscriberResponse, String>> {
        val subscriptionReverses = subscriptionRetriever.getSubscribers(
            serviceType = ServiceType.TWEETER,
            subscriptionType = subscriptionType,
            targetId = targetId,
            cursorRequest = cursorRequest,
        )
        return ApiResponse.success(
            result = CursorResult.of(
                data = subscriptionReverses.data.map { subscriptionReverse ->
                    SubscriberResponse.of(subscriptionReverse)
                },
                cursor = subscriptionReverses.cursor,
            )
        )
    }

    @GetMapping("/subscriber/{subscriberId}/targets")
    suspend fun getSubscriptionTargets(
        @PathVariable subscriptionType: SubscriptionType,
        @PathVariable subscriberId: String,
        @Valid cursorRequest: CursorRequest,
    ): ApiResponse<CursorResult<SubscriptionTargetResponse, String>> {
        val subscriptions = subscriptionRetriever.getSubscriptionTargets(
            serviceType = ServiceType.TWEETER,
            subscriptionType = subscriptionType,
            subscriberId = subscriberId,
            cursorRequest = cursorRequest,
        )
        return ApiResponse.success(
            result = CursorResult.of(
                data = subscriptions.data.map { subscription ->
                    SubscriptionTargetResponse.of(subscription)
                },
                cursor = subscriptions.cursor,
            )
        )
    }

}
