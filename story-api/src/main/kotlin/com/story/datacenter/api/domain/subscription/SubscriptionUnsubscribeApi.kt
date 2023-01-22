package com.story.datacenter.api.domain.subscription

import com.story.datacenter.core.common.enums.ServiceType
import com.story.datacenter.core.common.model.ApiResponse
import com.story.datacenter.core.domain.subscription.SubscriptionUnSubscriber
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/v1/subscription/{subscriptionType}")
@RestController
class SubscriptionUnsubscribeApi(
    private val subscriptionUnSubscriber: SubscriptionUnSubscriber,
) {

    @DeleteMapping("/subscriber/{subscriberId}/target/{targetId}")
    suspend fun unsubscribe(
        @PathVariable subscriptionType: String,
        @PathVariable subscriberId: String,
        @PathVariable targetId: String,
    ): ApiResponse<String> {
        subscriptionUnSubscriber.unsubscribe(
            serviceType = ServiceType.TWEETER,
            subscriptionType = subscriptionType,
            targetId = targetId,
            subscriberId = subscriberId,
        )
        return ApiResponse.OK
    }

}
