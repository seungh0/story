package com.story.core.support.cache

import com.story.core.common.annotation.EventConsumer
import com.story.core.common.annotation.IOBound
import com.story.core.common.json.toObject
import com.story.core.common.logger.LoggerExtension.log
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
class CacheBroadcastEvictionConsumer(
    private val layeredCacheManager: LayeredCacheManager,

    @IOBound
    private val dispatcher: CoroutineDispatcher,
) {

    @RetryableKafkaListener(
        topics = ["\${story.kafka.topic.cache-broadcast-eviction.name}"],
        groupId = "$GROUP_ID-\${random.uuid}",
        containerFactory = KafkaConsumerConfig.DEFAULT_KAFKA_CONSUMER,
        concurrency = "3",
    )
    fun handleCacheEviction(
        @Payload record: ConsumerRecord<String, String>,
        @Headers headers: Map<String, Any>,
    ) = runBlocking {
        val message = record.value().toObject(CacheBroadcastEvictionMessage::class.java)
            ?: throw IllegalArgumentException("Record can't be deserialize, record: $record")

        withContext(dispatcher) {
            if (message.allEntries) {
                return@withContext layeredCacheManager.evictAllCachesLayeredCache(
                    cacheType = message.cacheType,
                    targetCacheStrategies = message.cacheStrategies,
                )
            }
            return@withContext layeredCacheManager.evictCacheLayeredCache(
                cacheType = message.cacheType,
                targetCacheStrategies = message.cacheStrategies,
                cacheKey = message.cacheKey,
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
            Cache Evict Consumer DLT is Received
            - record=$record
            - headers=$headers
            """.trimIndent()
        }
    }

    companion object {
        private const val GROUP_ID = "cache-evict-consumer"
    }

}
