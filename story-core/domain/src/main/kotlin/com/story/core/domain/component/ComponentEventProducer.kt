package com.story.core.domain.component

import com.story.core.common.annotation.EventProducer
import com.story.core.common.annotation.IOBound
import com.story.core.common.json.toJson
import com.story.core.domain.resource.ResourceId
import com.story.core.support.kafka.KafkaProducerConfig
import com.story.core.support.kafka.KafkaRecordKeyGenerator
import com.story.core.support.kafka.KafkaTopic
import com.story.core.support.kafka.send
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
