package com.story.core.domain.feed.mapping

import com.story.core.common.annotation.EventProducer
import com.story.core.common.annotation.IOBound
import com.story.core.common.json.toJson
import com.story.core.domain.resource.ResourceId
import com.story.core.support.kafka.KafkaProducerConfig
import com.story.core.support.kafka.KafkaRecordKeyGenerator
import com.story.core.support.kafka.KafkaTopic
import com.story.core.support.kafka.send
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.kafka.core.KafkaTemplate

@EventProducer
class FeedMappingEventProducer(
    @Qualifier(KafkaProducerConfig.FEED_MAPPING_KAFKA_PRODUCER)
    private val kafkaTemplate: KafkaTemplate<String, String>,

    @IOBound
    private val dispatcher: CoroutineDispatcher,
) {

    suspend fun publishCreatedEvent(
        workspaceId: String,
        feedComponentId: String,
        sourceResourceId: ResourceId,
        sourceComponentId: String,
        subscriptionComponentId: String,
    ) {
        val event = FeedMappingEvent.created(
            workspaceId = workspaceId,
            feedComponentId = feedComponentId,
            sourceResourceId = sourceResourceId,
            sourceComponentId = sourceComponentId,
            subscriptionComponentId = subscriptionComponentId,
        )
        withContext(dispatcher) {
            kafkaTemplate.send(
                kafkaTopic = KafkaTopic.FEED_MAPPING,
                key = KafkaRecordKeyGenerator.feedMapping(
                    workspaceId = workspaceId,
                    sourceResourceId = sourceResourceId,
                    sourceComponentId = sourceComponentId,
                ),
                data = event.toJson()
            )
        }
    }

    suspend fun publishDeletedEvent(
        workspaceId: String,
        feedComponentId: String,
        sourceResourceId: ResourceId,
        sourceComponentId: String,
        subscriptionComponentId: String,
    ) {
        val event = FeedMappingEvent.deleted(
            workspaceId = workspaceId,
            feedComponentId = feedComponentId,
            sourceResourceId = sourceResourceId,
            sourceComponentId = sourceComponentId,
            subscriptionComponentId = subscriptionComponentId,
        )
        withContext(dispatcher) {
            kafkaTemplate.send(
                kafkaTopic = KafkaTopic.FEED_MAPPING,
                key = KafkaRecordKeyGenerator.feedMapping(
                    workspaceId = workspaceId,
                    sourceResourceId = sourceResourceId,
                    sourceComponentId = sourceComponentId,
                ),
                data = event.toJson()
            )
        }
    }

}
