package com.story.platform.core.domain.subscription

import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.infrastructure.kafka.KafkaProducerConfig
import com.story.platform.core.infrastructure.kafka.KafkaTopicFinder
import com.story.platform.core.infrastructure.kafka.TopicType
import com.story.platform.core.support.json.toJson
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class SubscriptionEventPublisher(
    @Qualifier(KafkaProducerConfig.SUBSCRIPTION_KAFKA_TEMPLATE)
    private val kafkaTemplate: KafkaTemplate<String, String>,
) {

    suspend fun publishSubscriptionEvent(
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        subscriberId: String,
        targetId: String,
    ) {
        val event = SubscriptionEvent.created(
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            subscriberId = subscriberId,
            targetId = targetId,
        )
        kafkaTemplate.send(KafkaTopicFinder.getTopicName(TopicType.SUBSCRIPTION), subscriberId, event.toJson())
    }

    suspend fun publishUnsubscriptionEvent(
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        subscriberId: String,
        targetId: String,
    ) {
        val event = SubscriptionEvent.deleted(
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            subscriberId = subscriberId,
            targetId = targetId,
        )
        kafkaTemplate.send(KafkaTopicFinder.getTopicName(TopicType.SUBSCRIPTION), subscriberId, event.toJson())
    }

}