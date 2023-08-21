package com.story.platform.api.domain.component

import com.story.platform.core.common.coroutine.IOBound
import com.story.platform.core.common.json.toJson
import com.story.platform.core.common.json.toObject
import com.story.platform.core.common.logger.LoggerExtension.log
import com.story.platform.core.common.spring.EventConsumer
import com.story.platform.core.domain.component.ComponentEvent
import com.story.platform.core.domain.component.ComponentLocalCacheEvictManager
import com.story.platform.core.domain.event.EventAction
import com.story.platform.core.domain.event.EventRecord
import com.story.platform.core.infrastructure.kafka.KafkaConsumerConfig
import com.story.platform.core.infrastructure.kafka.KafkaProducerConfig
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.DltHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.annotation.RetryableTopic
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy
import org.springframework.messaging.handler.annotation.Headers
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.retry.annotation.Backoff

@EventConsumer
class ComponentCacheEvictConsumer(
    private val componentLocalCacheEvictManager: ComponentLocalCacheEvictManager,

    @IOBound
    private val dispatcher: CoroutineDispatcher,
) {

    @RetryableTopic(
        backoff = Backoff(delay = 200, multiplier = 1.5),
        attempts = "3",
        topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
        listenerContainerFactory = KafkaConsumerConfig.DEFAULT_KAFKA_CONSUMER,
        kafkaTemplate = KafkaProducerConfig.DEFAULT_ACK_ALL_KAFKA_PRODUCER,
        numPartitions = "3",
    )
    @KafkaListener(
        topics = ["\${story.kafka.topic.component.name}"],
        groupId = "$GROUP_ID-\${random.uuid}",
        containerFactory = KafkaConsumerConfig.DEFAULT_KAFKA_CONSUMER,
    )
    fun handleComponentCacheEviction(
        @Payload record: ConsumerRecord<String, String>,
        @Headers headers: Map<String, Any>,
    ) = runBlocking {
        val event = record.value().toObject(EventRecord::class.java)
            ?: throw IllegalArgumentException("Record can't be deserialize, record: $record")

        if (event.eventAction == EventAction.CREATED) {
            return@runBlocking
        }

        val payload = event.payload.toJson().toObject(ComponentEvent::class.java)
            ?: throw IllegalArgumentException("Record Payload can't be deserialize, record: $record")

        withContext(dispatcher) {
            componentLocalCacheEvictManager.evictComponent(
                workspaceId = payload.workspaceId,
                resourceId = payload.resourceId,
                componentId = payload.componentId,
            )
        }
    }

    @DltHandler
    fun dltHandler(
        @Payload record: ConsumerRecord<String, String>,
        @Headers headers: Map<String, Any>,
    ) = runBlocking {
        log.error {
            """
            Component Cache Evict Consumer DLT is Received
            - record=$record
            - headers=$headers
            """.trimIndent()
        }
    }

    companion object {
        private const val GROUP_ID = "component-cache-evict-consumer"
    }

}
