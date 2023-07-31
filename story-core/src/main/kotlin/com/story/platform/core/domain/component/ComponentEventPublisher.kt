package com.story.platform.core.domain.component

import com.story.platform.core.common.coroutine.IOBound
import com.story.platform.core.common.json.toJson
import com.story.platform.core.domain.event.EventRecord
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
class ComponentEventPublisher(
    @Qualifier(KafkaProducerConfig.DEFAULT_ACK_ALL_KAFKA_TEMPLATE)
    private val kafkaTemplate: KafkaTemplate<String, String>,

    @IOBound
    private val dispatcher: CoroutineDispatcher,
) {

    suspend fun publishEvent(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        event: EventRecord<ComponentEvent>,
    ) {
        withContext(dispatcher) {
            kafkaTemplate.send(
                KafkaTopicFinder.getTopicName(TopicType.COMPONENT),
                "$workspaceId:$resourceId:$componentId",
                event.toJson()
            )
        }
    }

}
