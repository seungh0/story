package com.story.platform.core.domain.feed

import com.story.platform.core.common.coroutine.IOBound
import com.story.platform.core.common.json.toJson
import com.story.platform.core.common.spring.EventProducer
import com.story.platform.core.domain.event.EventHistoryManager
import com.story.platform.core.domain.event.EventRecord
import com.story.platform.core.domain.resource.ResourceId
import com.story.platform.core.infrastructure.kafka.KafkaProducerConfig
import com.story.platform.core.infrastructure.kafka.KafkaRecordKeyGenerator
import com.story.platform.core.infrastructure.kafka.TopicType
import com.story.platform.core.infrastructure.kafka.send
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.kafka.core.KafkaTemplate

@EventProducer
class FeedEventProducer(
    @Qualifier(KafkaProducerConfig.FEED_KAFKA_TEMPLATE)
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val eventHistoryManager: EventHistoryManager,

    @IOBound
    private val dispatcher: CoroutineDispatcher,
) {

    suspend fun publishEvent(event: EventRecord<FeedEvent>) {
        eventHistoryManager.withSaveEventHistory(
            workspaceId = event.payload.workspaceId,
            resourceId = ResourceId.SUBSCRIPTIONS,
            componentId = event.payload.feedComponentId,
            event = event,
        ) {
            withContext(dispatcher) {
                kafkaTemplate.send(
                    topicType = TopicType.FEED,
                    key = KafkaRecordKeyGenerator.feed(eventKey = event.eventKey, slotId = event.payload.slotId),
                    data = event.toJson(),
                )
            }
        }
    }

}
