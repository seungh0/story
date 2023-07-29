package com.story.platform.api.domain.cache

import com.story.platform.core.common.coroutine.IOBound
import com.story.platform.core.common.error.NotSupportedException
import com.story.platform.core.common.json.JsonUtils
import com.story.platform.core.common.json.toJson
import com.story.platform.core.domain.authentication.AuthenticationKeyCacheEvictEvent
import com.story.platform.core.domain.authentication.AuthenticationKeyLocalCacheEvictionManager
import com.story.platform.core.domain.component.ComponentLocalCacheEvictEvent
import com.story.platform.core.domain.component.ComponentLocalCacheEvictionManager
import com.story.platform.core.infrastructure.kafka.KafkaConsumerConfig
import com.story.platform.core.support.cache.CacheEvictEventRecord
import com.story.platform.core.support.cache.CacheType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Headers
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service

@Service
class LocalCacheConsumer(
    private val componentLocalCacheEvictionManager: ComponentLocalCacheEvictionManager,
    private val authenticationKeyLocalCacheEvictionManager: AuthenticationKeyLocalCacheEvictionManager,

    @IOBound
    private val dispatcher: CoroutineDispatcher,
) {

    @KafkaListener(
        topics = ["\${story.kafka.cache-evict.topic}"],
        groupId = "\${story.kafka.cache-evict.group-id}",
        containerFactory = KafkaConsumerConfig.POST_CONTAINER_FACTORY,
    )
    fun evictLocalCache(
        @Payload record: ConsumerRecord<String, String>,
        @Headers headers: Map<String, Any>,
    ) = runBlocking {
        val event = JsonUtils.toObject(record.value(), CacheEvictEventRecord::class.java)
            ?: throw IllegalArgumentException("Record can't be deserialize, record: $record")

        withContext(dispatcher) {
            when (event.cacheType) {
                CacheType.COMPONENT -> {
                    val payload = JsonUtils.toObject(event.payload.toJson(), ComponentLocalCacheEvictEvent::class.java)
                        ?: throw IllegalArgumentException("Record Payload can't be deserialize, record: $record")
                    componentLocalCacheEvictionManager.evictComponent(
                        workspaceId = payload.workspaceId,
                        resourceId = payload.resourceId,
                        componentId = payload.componentId,
                    )
                }

                CacheType.AUTHENTICATION_REVERSE_KEY -> {
                    val payload = JsonUtils.toObject(
                        event.payload.toJson(),
                        AuthenticationKeyCacheEvictEvent::class.java
                    )
                        ?: throw IllegalArgumentException("Record Payload can't be deserialize, record: $record")
                    authenticationKeyLocalCacheEvictionManager.evictAuthenticationKey(
                        authenticationKey = payload.authenticationKey,
                    )
                }

                else -> throw NotSupportedException("현재 로컬 캐시(${event.cacheType}) 만료가 제공되지 않습니다")
            }
        }
    }

}
