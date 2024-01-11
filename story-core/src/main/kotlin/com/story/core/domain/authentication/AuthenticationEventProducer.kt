package com.story.core.domain.authentication

import com.story.core.common.annotation.EventProducer
import com.story.core.common.annotation.IOBound
import com.story.core.common.json.toJson
import com.story.core.domain.event.EventRecord
import com.story.core.infrastructure.kafka.KafkaProducerConfig
import com.story.core.infrastructure.kafka.KafkaRecordKeyGenerator
import com.story.core.infrastructure.kafka.KafkaTopic
import com.story.core.infrastructure.kafka.send
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.kafka.core.KafkaTemplate

@EventProducer
class AuthenticationEventProducer(
    @IOBound
    private val dispatcher: CoroutineDispatcher,

    @Qualifier(KafkaProducerConfig.DEFAULT_ACK_ALL_KAFKA_PRODUCER)
    private val kafkaTemplate: KafkaTemplate<String, String>,
) {

    suspend fun publishEvent(authenticationKey: String, event: EventRecord<AuthenticationEvent>) {
        withContext(dispatcher) {
            kafkaTemplate.send(
                kafkaTopic = KafkaTopic.AUTHENTICATION,
                key = KafkaRecordKeyGenerator.authentication(authenticationKey = authenticationKey),
                data = event.toJson()
            )
        }
    }

}
