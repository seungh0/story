package com.story.platform.api.domain.authentication

import com.story.platform.core.common.coroutine.IOBound
import com.story.platform.core.common.json.JsonUtils
import com.story.platform.core.common.json.toJson
import com.story.platform.core.domain.authentication.AuthenticationKeyEvent
import com.story.platform.core.domain.authentication.AuthenticationKeyLocalCacheEvictionManager
import com.story.platform.core.domain.event.EventAction
import com.story.platform.core.domain.event.EventRecord
import com.story.platform.core.infrastructure.kafka.KafkaConsumerConfig
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Headers
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service

@Service
class AuthenticationKeyLocalCacheEvictConsumer(
    private val authenticationKeyLocalCacheEvictionManager: AuthenticationKeyLocalCacheEvictionManager,

    @IOBound
    private val dispatcher: CoroutineDispatcher,
) {

    @KafkaListener(
        topics = ["\${story.kafka.authentication-key.topic}"],
        groupId = "\${story.server-uid}",
        containerFactory = KafkaConsumerConfig.AUTHENTICATION_KEY_CONTAINER_FACTORY,
    )
    fun handleAuthenticationKeyCacheEviction(
        @Payload record: ConsumerRecord<String, String>,
        @Headers headers: Map<String, Any>,
    ) = runBlocking {
        val event = JsonUtils.toObject(record.value(), EventRecord::class.java)
            ?: throw IllegalArgumentException("Record can't be deserialize, record: $record")

        if (event.eventAction != EventAction.UPDATED) {
            return@runBlocking
        }

        val payload = JsonUtils.toObject(event.payload.toJson(), AuthenticationKeyEvent::class.java)
            ?: throw IllegalArgumentException("Record Payload can't be deserialize, record: $record")

        withContext(dispatcher) {
            authenticationKeyLocalCacheEvictionManager.evictAuthenticationKey(
                authenticationKey = payload.authenticationKey,
            )
        }
    }

}
