package com.story.platform.core.domain.component

import com.story.platform.core.common.coroutine.IOBound
import com.story.platform.core.common.json.toJson
import com.story.platform.core.common.spring.EventProducer
import com.story.platform.core.domain.resource.ResourceId
import com.story.platform.core.infrastructure.kafka.KafkaProducerConfig
import com.story.platform.core.infrastructure.kafka.KafkaRecordKeyGenerator
import com.story.platform.core.infrastructure.kafka.KafkaTopic
import com.story.platform.core.infrastructure.kafka.send
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.kafka.core.KafkaTemplate

@EventProducer
class ComponentEventProducer(
    @Qualifier(KafkaProducerConfig.DEFAULT_ACK_ALL_KAFKA_PRODUCER)
    private val kafkaTemplate: KafkaTemplate<String, String>,

    @IOBound
    private val dispatcher: CoroutineDispatcher,
) {

    suspend fun publishUpdatedEvent(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
    ) {
        withContext(dispatcher) {
            kafkaTemplate.send(
                kafkaTopic = KafkaTopic.COMPONENT,
                key = KafkaRecordKeyGenerator.component(
                    workspaceId = workspaceId,
                    resourceId = resourceId,
                    componentId = componentId,
                ),
                data = ComponentEvent.updated(
                    workspaceId = workspaceId,
                    resourceId = resourceId,
                    componentId = componentId,
                ).toJson()
            )
        }
    }

}
