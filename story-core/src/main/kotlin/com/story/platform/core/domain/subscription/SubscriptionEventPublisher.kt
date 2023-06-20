package com.story.platform.core.domain.subscription

import com.story.platform.core.domain.event.EventHistoryManager
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
    private val eventHistoryManager: EventHistoryManager,
) {

    suspend fun publishSubscriptionEvent(
        workspaceId: String,
        subscriptionType: SubscriptionType,
        subscriberId: String,
        targetId: String,
    ) {
        val event = SubscriptionEvent.subscribed(
            workspaceId = workspaceId,
            subscriptionType = subscriptionType,
            subscriberId = subscriberId,
            targetId = targetId,
        )

        eventHistoryManager.withSaveEventHistory(
            workspaceId = workspaceId,
            event = event,
        ) {
            kafkaTemplate.send(KafkaTopicFinder.getTopicName(TopicType.SUBSCRIPTION), subscriberId, event.toJson())
        }
    }

    suspend fun publishUnsubscriptionEvent(
        workspaceId: String,
        subscriptionType: SubscriptionType,
        subscriberId: String,
        targetId: String,
    ) {
        val event = SubscriptionEvent.unsubscribed(
            workspaceId = workspaceId,
            subscriptionType = subscriptionType,
            subscriberId = subscriberId,
            targetId = targetId,
        )

        eventHistoryManager.withSaveEventHistory(
            workspaceId = workspaceId,
            event = event,
        ) {
            kafkaTemplate.send(KafkaTopicFinder.getTopicName(TopicType.SUBSCRIPTION), subscriberId, event.toJson())
        }
    }

}
