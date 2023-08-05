package com.story.platform.core.domain.subscription

import com.story.platform.core.common.coroutine.IOBound
import com.story.platform.core.common.json.toJson
import com.story.platform.core.common.spring.EventProducer
import com.story.platform.core.domain.event.EventHistoryManager
import com.story.platform.core.domain.resource.ResourceId
import com.story.platform.core.infrastructure.kafka.KafkaProducerConfig
import com.story.platform.core.infrastructure.kafka.TopicType
import com.story.platform.core.infrastructure.kafka.send
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.kafka.core.KafkaTemplate

@EventProducer
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
            resourceId = ResourceId.SUBSCRIPTIONS,
            componentId = componentId,
            event = event,
        ) {
            withContext(dispatcher) {
                kafkaTemplate.send(topicType = TopicType.SUBSCRIPTION, key = subscriberId, data = event.toJson())
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
            resourceId = ResourceId.SUBSCRIPTIONS,
            componentId = componentId,
            event = event,
        ) {
            withContext(dispatcher) {
                kafkaTemplate.send(topicType = TopicType.SUBSCRIPTION, key = subscriberId, data = event.toJson())
            }
        }
    }

}
