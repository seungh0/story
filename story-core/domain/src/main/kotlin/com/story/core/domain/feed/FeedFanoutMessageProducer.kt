package com.story.core.domain.feed

import com.story.core.common.annotation.EventProducer
import com.story.core.common.annotation.IOBound
import com.story.core.common.json.toJson
import com.story.core.domain.event.EventRecord
import com.story.core.support.kafka.KafkaProducerConfig
import com.story.core.support.kafka.KafkaRecordKeyGenerator
import com.story.core.support.kafka.KafkaTopic
import com.story.core.support.kafka.send
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.kafka.core.KafkaTemplate

@EventProducer
class FeedFanoutMessageProducer(
    @Qualifier(KafkaProducerConfig.FEED_KAFKA_PRODUCER)
    private val kafkaTemplate: KafkaTemplate<String, String>,

    @IOBound
    private val dispatcher: CoroutineDispatcher,
) {

    suspend fun publish(event: EventRecord<FeedFanoutMessage>) {
        withContext(dispatcher) {
            kafkaTemplate.send(
                kafkaTopic = KafkaTopic.FEED_FANOUT,
                key = KafkaRecordKeyGenerator.feed(
                    workspaceId = event.payload.workspaceId,
                    componentId = event.payload.componentId,
                    itemResourceId = event.payload.item.resourceId,
                    itemComponentId = event.payload.item.componentId,
                    itemId = event.payload.item.itemId,
                ),
                data = event.toJson(),
            )
        }
    }

}
