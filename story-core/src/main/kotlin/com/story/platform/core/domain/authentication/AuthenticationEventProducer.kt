package com.story.platform.core.domain.authentication

import com.story.platform.core.common.coroutine.IOBound
import com.story.platform.core.common.json.toJson
import com.story.platform.core.common.spring.EventProducer
import com.story.platform.core.domain.event.EventRecord
import com.story.platform.core.infrastructure.kafka.KafkaProducerConfig
import com.story.platform.core.infrastructure.kafka.KafkaRecordKeyGenerator
import com.story.platform.core.infrastructure.kafka.KafkaTopic
import com.story.platform.core.infrastructure.kafka.send
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
