package com.story.platform.core.domain.subscription

import com.story.platform.core.common.coroutine.IOBound
import com.story.platform.core.common.json.toJson
import com.story.platform.core.domain.event.EventHistoryManager
import com.story.platform.core.infrastructure.kafka.KafkaProducerConfig
import com.story.platform.core.infrastructure.kafka.KafkaTopicFinder
import com.story.platform.core.infrastructure.kafka.TopicType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class SubscriptionEventPublisher(
    @Qualifier(KafkaProducerConfig.SUBSCRIPTION_KAFKA_TEMPLATE)
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val eventHistoryManager: EventHistoryManager,

    @IOBound
    private val dispatcher: CoroutineDispatcher,
) {

    suspend fun publishSubscriptionEvent(
        workspaceId: String,
        componentId: String,
        subscriberId: String,
        targetId: String,
    ) {
        val event = SubscriptionEvent.subscribed(
            workspaceId = workspaceId,
            componentId = componentId,
            subscriberId = subscriberId,
            targetId = targetId,
        )

        eventHistoryManager.withSaveEventHistory(
            workspaceId = workspaceId,
            componentId = componentId,
            event = event,
        ) {
            withContext(dispatcher) {
                kafkaTemplate.send(KafkaTopicFinder.getTopicName(TopicType.SUBSCRIPTION), subscriberId, event.toJson())
            }
        }
    }

    suspend fun publishUnsubscriptionEvent(
        workspaceId: String,
        componentId: String,
        subscriberId: String,
        targetId: String,
    ) {
        val event = SubscriptionEvent.unsubscribed(
            workspaceId = workspaceId,
            componentId = componentId,
            subscriberId = subscriberId,
            targetId = targetId,
        )

        eventHistoryManager.withSaveEventHistory(
            workspaceId = workspaceId,
            componentId = componentId,
            event = event,
        ) {
            withContext(dispatcher) {
                kafkaTemplate.send(KafkaTopicFinder.getTopicName(TopicType.SUBSCRIPTION), subscriberId, event.toJson())
            }
        }
    }

}
