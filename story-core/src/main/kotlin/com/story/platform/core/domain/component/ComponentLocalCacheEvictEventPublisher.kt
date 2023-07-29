package com.story.platform.core.domain.component

import com.story.platform.core.common.coroutine.IOBound
import com.story.platform.core.common.json.toJson
import com.story.platform.core.domain.resource.ResourceId
import com.story.platform.core.infrastructure.kafka.KafkaProducerConfig
import com.story.platform.core.infrastructure.kafka.KafkaTopicFinder
import com.story.platform.core.infrastructure.kafka.TopicType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class ComponentLocalCacheEvictEventPublisher(
    @Qualifier(KafkaProducerConfig.DEFAULT_ACK_ALL_KAFKA_TEMPLATE)
    private val kafkaTemplate: KafkaTemplate<String, String>,
    @IOBound
    private val dispatcher: CoroutineDispatcher,
) {

    suspend fun publishedEvent(workspaceId: String, resourceId: ResourceId, componentId: String) {
        val event = ComponentLocalCacheEvictEvent.of(
            workspaceId = workspaceId,
            resourceId = resourceId,
            componentId = componentId
        )
        withContext(dispatcher) {
            kafkaTemplate.send(
                KafkaTopicFinder.getTopicName(TopicType.LOCAL_CACHE_EVICT),
                event.toJson()
            )
        }
    }

}
