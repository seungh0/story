package com.story.datacenter.api.domain.subscription

import com.story.datacenter.core.common.enums.ServiceType
import com.story.datacenter.core.common.model.ApiResponse
import com.story.datacenter.core.common.model.CursorRequest
import com.story.datacenter.core.common.model.CursorResult
import com.story.datacenter.core.domain.subscription.SubscriptionResponse
import com.story.datacenter.core.domain.subscription.SubscriptionRetriever
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RequestMapping("/v1/subscription/{subscriptionType}")
@RestController
class SubscriptionRetrieverApi(
    private val subscriptionRetriever: SubscriptionRetriever,
) {

    @GetMapping("/subscriber/{subscriberId}/target/{targetId}/exists")
    suspend fun checkSubscription(
        @PathVariable subscriptionType: String,
        @PathVariable subscriberId: String,
        @PathVariable targetId: String,
    ): ApiResponse<SubscriptionExistsResponse> {
        val exists = subscriptionRetriever.checkSubscription(
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
    suspend fun getSubscribersCount(
        @PathVariable subscriptionType: String,
        @PathVariable targetId: String,
    ): ApiResponse<SubscribersCountResponse> {
        val subscribersCount = subscriptionRetriever.getSubscribersCount(
            serviceType = ServiceType.TWEETER,
            subscriptionType = subscriptionType,
            targetId = targetId,
        )
        return ApiResponse.success(
            result = SubscribersCountResponse(count = subscribersCount)
        )
    }

    @GetMapping("/target/{targetId}/subscribers")
    suspend fun getTargetSubscribers(
        @PathVariable subscriptionType: String,
        @PathVariable targetId: String,
        @Valid cursorRequest: CursorRequest,
    ): ApiResponse<CursorResult<SubscriptionResponse, String>> {
        return ApiResponse.success(
            result = subscriptionRetriever.getTargetSubscribers(
                serviceType = ServiceType.TWEETER,
                subscriptionType = subscriptionType,
                targetId = targetId,
                cursorRequest = cursorRequest,
            )
        )
    }

    @GetMapping("/subscriber/{subscriberId}/targets")
    suspend fun getSubscriberTargets(
        @PathVariable subscriptionType: String,
        @PathVariable subscriberId: String,
        @Valid cursorRequest: CursorRequest,
    ): ApiResponse<CursorResult<SubscriptionResponse, String>> {
        return ApiResponse.success(
            result = subscriptionRetriever.getSubscriberTargets(
                serviceType = ServiceType.TWEETER,
                subscriptionType = subscriptionType,
                subscriberId = subscriberId,
                cursorRequest = cursorRequest,
            )
        )
    }

}
