package com.story.core.support.cache

import com.story.core.common.annotation.EventProducer
import com.story.core.common.annotation.IOBound
import com.story.core.common.json.toJson
import com.story.core.support.kafka.KafkaProducerConfig
import com.story.core.support.kafka.KafkaTopic
import com.story.core.support.kafka.send
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.kafka.core.KafkaTemplate

@EventProducer
class CacheBroadcastEvictionProducer(
    @IOBound
    private val dispatcher: CoroutineDispatcher,

    @Qualifier(KafkaProducerConfig.DEFAULT_ACK_ALL_KAFKA_PRODUCER)
    private val kafkaTemplate: KafkaTemplate<String, String>,
) {

    suspend fun targetKey(cacheType: CacheType, cacheKey: String) {
        val message = CacheBroadcastEvictionMessage.targetKey(
            cacheType = cacheType,
            cacheKey = cacheKey,
            cacheStrategies = setOf(CacheStrategy.LOCAL),
        )

        withContext(dispatcher) {
            kafkaTemplate.send(
                kafkaTopic = KafkaTopic.CACHE_EVICTION,
                data = message.toJson()
            )
        }
    }

    suspend fun allEntries(cacheType: CacheType) {
        val message = CacheBroadcastEvictionMessage.allEntries(
            cacheType = cacheType,
            cacheStrategies = setOf(CacheStrategy.LOCAL),
        )

        withContext(dispatcher) {
            kafkaTemplate.send(
                kafkaTopic = KafkaTopic.CACHE_EVICTION,
                data = message.toJson()
            )
        }
    }

}
