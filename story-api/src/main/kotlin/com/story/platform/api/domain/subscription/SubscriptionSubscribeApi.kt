package com.story.platform.api.domain.subscription

import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.common.model.ApiResponse
import com.story.platform.core.common.utils.JsonUtils
import com.story.platform.core.domain.subscription.SubscriptionEvent
import com.story.platform.core.domain.subscription.SubscriptionEventType
import com.story.platform.core.domain.subscription.SubscriptionSubscriber
import com.story.platform.core.domain.subscription.SubscriptionType
import com.story.platform.core.support.kafka.KafkaTopicFinder
import com.story.platform.core.support.kafka.TopicType
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/v1/subscription/{subscriptionType}")
@RestController
class SubscriptionSubscribeApi(
    private val subscriptionSubscriber: SubscriptionSubscriber,
    private val kafkaTemplate: KafkaTemplate<String, String>,
) {

    @PostMapping("/subscriber/{subscriberId}/target/{targetId}")
    suspend fun subscribe(
        @PathVariable subscriptionType: SubscriptionType,
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

    @PostMapping("/subscriber/{subscriberId}/target/{targetId}/async")
    suspend fun subscribeAsync(
        @PathVariable subscriptionType: SubscriptionType,
        @PathVariable subscriberId: String,
        @PathVariable targetId: String,
    ): ApiResponse<String> {
        val event = SubscriptionEvent(
            type = SubscriptionEventType.SUBSCRIPTION,
            serviceType = ServiceType.TWEETER,
            subscriptionType = subscriptionType,
            subscriberId = subscriberId,
            targetId = targetId,
        )
        kafkaTemplate.send(KafkaTopicFinder.getTopicName(TopicType.SUBSCRIPTION), JsonUtils.toJson(event))
        return ApiResponse.OK
    }

}
