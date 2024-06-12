package com.story.core.domain.apikey

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
class ApiKeyEventProducer(
    @IOBound
    private val dispatcher: CoroutineDispatcher,

    @Qualifier(KafkaProducerConfig.DEFAULT_ACK_ALL_KAFKA_PRODUCER)
    private val kafkaTemplate: KafkaTemplate<String, String>,
) {

    suspend fun publishEvent(apiKey: String, event: EventRecord<ApiKeyEvent>) {
        withContext(dispatcher) {
            kafkaTemplate.send(
                kafkaTopic = KafkaTopic.API_KRY,
                key = KafkaRecordKeyGenerator.apiKey(key = apiKey),
                data = event.toJson()
            )
        }
    }

}
