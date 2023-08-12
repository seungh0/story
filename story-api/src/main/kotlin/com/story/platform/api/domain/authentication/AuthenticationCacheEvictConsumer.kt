package com.story.platform.api.domain.authentication

import com.story.platform.core.common.coroutine.IOBound
import com.story.platform.core.common.json.toJson
import com.story.platform.core.common.json.toObject
import com.story.platform.core.common.spring.EventConsumer
import com.story.platform.core.domain.authentication.AuthenticationEvent
import com.story.platform.core.domain.authentication.AuthenticationLocalCacheEvictManager
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

@EventConsumer
class AuthenticationCacheEvictConsumer(
    private val authenticationLocalCacheEvictManager: AuthenticationLocalCacheEvictManager,

    @IOBound
    private val dispatcher: CoroutineDispatcher,
) {

    @KafkaListener(
        topics = ["\${story.kafka.topic.authentication}"],
        groupId = GROUP_ID,
        containerFactory = KafkaConsumerConfig.AUTHENTICATION_KEY_CONTAINER_FACTORY,
    )
    fun handleAuthenticationKeyCacheEviction(
        @Payload record: ConsumerRecord<String, String>,
        @Headers headers: Map<String, Any>,
    ) = runBlocking {
        val event = record.value().toObject(EventRecord::class.java)
            ?: throw IllegalArgumentException("Record can't be deserialize, record: $record")

        if (event.eventAction == EventAction.CREATED) {
            return@runBlocking
        }

        val payload = event.payload.toJson().toObject(AuthenticationEvent::class.java)
            ?: throw IllegalArgumentException("Record Payload can't be deserialize, record: $record")

        withContext(dispatcher) {
            authenticationLocalCacheEvictManager.evictAuthenticationKey(
                authenticationKey = payload.authenticationKey,
            )
        }
    }

    companion object {
        private const val GROUP_ID = "authentication-key-cache-evict-consumer"
    }

}
