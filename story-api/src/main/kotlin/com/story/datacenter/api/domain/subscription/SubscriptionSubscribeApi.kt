package com.story.datacenter.api.domain.subscription

import com.story.datacenter.core.common.enums.ServiceType
import com.story.datacenter.core.common.model.ApiResponse
import com.story.datacenter.core.domain.subscription.SubscriptionSubscriber
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/v1/subscription/{subscriptionType}")
@RestController
class SubscriptionSubscribeApi(
    private val subscriptionSubscriber: SubscriptionSubscriber,
) {

    @PostMapping("/subscriber/{subscriberId}/target/{targetId}")
    suspend fun subscribe(
        @PathVariable subscriptionType: String,
        @PathVariable subscriberId: String,
        @PathVariable targetId: String,
    ): ApiResponse<String> {
        subscriptionSubscriber.subscribe(
            serviceType = ServiceType.TWEETER,
            subscriptionType = subscriptionType,
            targetId = targetId,
            subscriberId = subscriberId,
        )
        return ApiResponse.OK
    }

}
