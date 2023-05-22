package com.story.platform.api.domain.subscription

import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.common.model.ApiResponse
import com.story.platform.core.domain.subscription.SubscriptionSubscriber
import com.story.platform.core.domain.subscription.SubscriptionType
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class SubscriptionSubscribeApi(
    private val subscriptionSubscriber: SubscriptionSubscriber,
) {

    /**
     * 구독을 등록한다
     */
    @PostMapping("/v1/subscriptions/{subscriptionType}/subscribers/{subscriberId}/targets/{targetId}")
    suspend fun subscribe(
        @PathVariable subscriptionType: SubscriptionType,
        @PathVariable subscriberId: String,
        @PathVariable targetId: String,
        @Valid @RequestBody request: SubscriptionSubscribeApiRequest,
    ): ApiResponse<String> {
        subscriptionSubscriber.subscribe(
            serviceType = ServiceType.TWEETER,
            subscriptionType = subscriptionType,
            targetId = targetId,
            subscriberId = subscriberId,
            alarm = request.alarm,
        )
        return ApiResponse.OK
    }

}
