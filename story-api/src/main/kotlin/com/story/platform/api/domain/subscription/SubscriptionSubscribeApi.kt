package com.story.platform.api.domain.subscription

import com.story.platform.core.domain.subscription.SubscriptionSubscriber
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
    ): com.story.platform.core.common.model.ApiResponse<String> {
        subscriptionSubscriber.subscribe(
            serviceType = com.story.platform.core.common.enums.ServiceType.TWEETER,
            subscriptionType = subscriptionType,
            targetId = targetId,
            subscriberId = subscriberId,
        )
        return com.story.platform.core.common.model.ApiResponse.OK
    }

}
