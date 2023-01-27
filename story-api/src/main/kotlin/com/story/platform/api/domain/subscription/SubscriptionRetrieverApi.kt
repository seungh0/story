package com.story.platform.api.domain.subscription

import com.story.platform.core.domain.subscription.SubscriptionResponse
import com.story.platform.core.domain.subscription.SubscriptionRetriever
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
    ): com.story.platform.core.common.model.ApiResponse<SubscriptionExistsResponse> {
        val exists = subscriptionRetriever.checkSubscription(
            serviceType = com.story.platform.core.common.enums.ServiceType.TWEETER,
            subscriptionType = subscriptionType,
            targetId = targetId,
            subscriberId = subscriberId,
        )
        return com.story.platform.core.common.model.ApiResponse.success(
            result = SubscriptionExistsResponse(exists = exists)
        )
    }

    @GetMapping("/target/{targetId}/subscribers/count")
    suspend fun getSubscribersCount(
        @PathVariable subscriptionType: String,
        @PathVariable targetId: String,
    ): com.story.platform.core.common.model.ApiResponse<SubscribersCountResponse> {
        val subscribersCount = subscriptionRetriever.getSubscribersCount(
            serviceType = com.story.platform.core.common.enums.ServiceType.TWEETER,
            subscriptionType = subscriptionType,
            targetId = targetId,
        )
        return com.story.platform.core.common.model.ApiResponse.success(
            result = SubscribersCountResponse(count = subscribersCount)
        )
    }

    @GetMapping("/target/{targetId}/subscribers")
    suspend fun getTargetSubscribers(
        @PathVariable subscriptionType: String,
        @PathVariable targetId: String,
        @Valid cursorRequest: com.story.platform.core.common.model.CursorRequest,
    ): com.story.platform.core.common.model.ApiResponse<com.story.platform.core.common.model.CursorResult<SubscriptionResponse, String>> {
        return com.story.platform.core.common.model.ApiResponse.success(
            result = subscriptionRetriever.getTargetSubscribers(
                serviceType = com.story.platform.core.common.enums.ServiceType.TWEETER,
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
        @Valid cursorRequest: com.story.platform.core.common.model.CursorRequest,
    ): com.story.platform.core.common.model.ApiResponse<com.story.platform.core.common.model.CursorResult<SubscriptionResponse, String>> {
        return com.story.platform.core.common.model.ApiResponse.success(
            result = subscriptionRetriever.getSubscriberTargets(
                serviceType = com.story.platform.core.common.enums.ServiceType.TWEETER,
                subscriptionType = subscriptionType,
                subscriberId = subscriberId,
                cursorRequest = cursorRequest,
            )
        )
    }

}
