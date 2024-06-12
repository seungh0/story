package com.story.api.application.apikey

import com.story.core.common.annotation.EventConsumer
import com.story.core.common.annotation.IOBound
import com.story.core.common.json.toJson
import com.story.core.common.json.toObject
import com.story.core.common.logger.LoggerExtension.log
import com.story.core.domain.apikey.ApiKeyEvent
import com.story.core.domain.apikey.ApiKeyLocalCacheEvictManager
import com.story.core.domain.event.EventAction
import com.story.core.domain.event.EventRecord
import com.story.core.support.kafka.KafkaConsumerConfig
import com.story.core.support.kafka.RetryableKafkaListener
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.DltHandler
import org.springframework.messaging.handler.annotation.Headers
import org.springframework.messaging.handler.annotation.Payload

@EventConsumer
class ApiKeyCacheEvictConsumer(
    private val apiKeyLocalCacheEvictManager: ApiKeyLocalCacheEvictManager,

    @IOBound
    private val dispatcher: CoroutineDispatcher,
) {

    @RetryableKafkaListener(
        topics = ["\${story.kafka.topic.api-key.name}"],
        groupId = "$GROUP_ID-\${random.uuid}",
        containerFactory = KafkaConsumerConfig.DEFAULT_KAFKA_CONSUMER,
    )
    fun handleApiKeyCacheEviction(
        @Payload record: ConsumerRecord<String, String>,
        @Headers headers: Map<String, Any>,
    ) = runBlocking {
        val event = record.value().toObject(EventRecord::class.java)
            ?: throw IllegalArgumentException("Record can't be deserialize, record: $record")

        if (event.eventAction == EventAction.CREATED) {
            return@runBlocking
        }

        val payload = event.payload.toJson().toObject(ApiKeyEvent::class.java)
            ?: throw IllegalArgumentException("Record Payload can't be deserialize, record: $record")

        withContext(dispatcher) {
            apiKeyLocalCacheEvictManager.evictApiKey(
                apiKey = payload.apiKey,
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
            ApiKey Cache Evict Consumer DLT is Received
            - record=$record
            - headers=$headers
            """.trimIndent()
        }
    }

    companion object {
        private const val GROUP_ID = "api-key-cache-evict-consumer"
    }

}
