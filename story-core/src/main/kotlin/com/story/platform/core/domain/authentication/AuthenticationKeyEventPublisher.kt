package com.story.platform.core.domain.authentication

import com.story.platform.core.common.coroutine.IOBound
import com.story.platform.core.common.json.toJson
import com.story.platform.core.domain.event.EventRecord
import com.story.platform.core.infrastructure.kafka.KafkaProducerConfig
import com.story.platform.core.infrastructure.kafka.TopicType
import com.story.platform.core.infrastructure.kafka.send
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class AuthenticationKeyEventPublisher(
    @IOBound
    private val dispatcher: CoroutineDispatcher,

    @Qualifier(KafkaProducerConfig.DEFAULT_ACK_ALL_KAFKA_TEMPLATE)
    private val kafkaTemplate: KafkaTemplate<String, String>,
) {

    suspend fun publishEvent(authenticationKey: String, event: EventRecord<AuthenticationKeyEvent>) {
        withContext(dispatcher) {
            kafkaTemplate.send(
                topicType = TopicType.AUTHENTICATION_KEY,
                key = authenticationKey,
                data = event.toJson()
            )
        }
    }

}
