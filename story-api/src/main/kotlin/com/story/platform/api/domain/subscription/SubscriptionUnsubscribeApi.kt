package com.story.platform.api.domain.subscription

import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.common.model.ApiResponse
import com.story.platform.core.common.utils.JsonUtils
import com.story.platform.core.domain.subscription.SubscriptionEvent
import com.story.platform.core.domain.subscription.SubscriptionEventType
import com.story.platform.core.domain.subscription.SubscriptionType
import com.story.platform.core.domain.subscription.SubscriptionUnSubscriber
import com.story.platform.core.support.kafka.KafkaTopicFinder
import com.story.platform.core.support.kafka.TopicType
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/v1/subscription/{subscriptionType}")
@RestController
class SubscriptionUnsubscribeApi(
    private val subscriptionUnSubscriber: SubscriptionUnSubscriber,
    private val kafkaTemplate: KafkaTemplate<String, String>,
) {

    @DeleteMapping("/subscriber/{subscriberId}/target/{targetId}")
    suspend fun unsubscribe(
        @PathVariable subscriptionType: SubscriptionType,
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

    @DeleteMapping("/subscriber/{subscriberId}/target/{targetId}/async")
    suspend fun unsubscribeAsync(
        @PathVariable subscriptionType: SubscriptionType,
        @PathVariable subscriberId: String,
        @PathVariable targetId: String,
    ): ApiResponse<String> {
        val event = SubscriptionEvent(
            type = SubscriptionEventType.UN_SUBSCRIPTION,
            serviceType = ServiceType.TWEETER,
            subscriptionType = subscriptionType,
            subscriberId = subscriberId,
            targetId = targetId,
        )
        kafkaTemplate.send(KafkaTopicFinder.getTopicName(TopicType.UNSUBSCRIPTION), JsonUtils.toJson(event))
        return ApiResponse.OK
    }

}
