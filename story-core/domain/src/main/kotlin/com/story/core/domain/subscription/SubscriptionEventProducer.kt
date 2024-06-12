package com.story.core.domain.subscription

import com.story.core.common.annotation.EventProducer
import com.story.core.common.annotation.IOBound
import com.story.core.common.json.toJson
import com.story.core.domain.event.EventHistoryManager
import com.story.core.domain.resource.ResourceId
import com.story.core.support.kafka.KafkaProducerConfig
import com.story.core.support.kafka.KafkaRecordKeyGenerator
import com.story.core.support.kafka.KafkaTopic
import com.story.core.support.kafka.send
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.kafka.core.KafkaTemplate
import java.time.LocalDateTime

@EventProducer
class SubscriptionEventProducer(
    @Qualifier(KafkaProducerConfig.SUBSCRIPTION_KAFKA_PRODUCER)
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val eventHistoryManager: EventHistoryManager,

    @IOBound
    private val dispatcher: CoroutineDispatcher,
) {

    suspend fun publishCreatedEvent(
        workspaceId: String,
        componentId: String,
        subscriberId: String,
        targetId: String,
        now: LocalDateTime = LocalDateTime.now(),
    ) {
        val event = SubscriptionEvent.subscribed(
            workspaceId = workspaceId,
            componentId = componentId,
            subscriberId = subscriberId,
            targetId = targetId,
            createdAt = now,
        )

        eventHistoryManager.withSaveEventHistory(
            workspaceId = workspaceId,
            resourceId = ResourceId.SUBSCRIPTIONS,
            componentId = componentId,
            event = event,
        ) {
            withContext(dispatcher) {
                kafkaTemplate.send(
                    kafkaTopic = KafkaTopic.SUBSCRIPTION,
                    key = KafkaRecordKeyGenerator.subscription(
                        workspaceId = workspaceId,
                        componentId = componentId,
                        subscriberId = subscriberId,
                    ),
                    data = event.toJson(),
                )
            }
        }
    }

    suspend fun publishRemovedEvent(
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
                kafkaTemplate.send(
                    kafkaTopic = KafkaTopic.SUBSCRIPTION,
                    key = KafkaRecordKeyGenerator.subscription(
                        workspaceId = workspaceId,
                        componentId = componentId,
                        subscriberId = subscriberId,
                    ),
                    data = event.toJson()
                )
            }
        }
    }

}
