package com.story.platform.api.domain.component

import com.story.platform.core.common.coroutine.IOBound
import com.story.platform.core.common.json.toJson
import com.story.platform.core.domain.component.ComponentEvent
import com.story.platform.core.domain.component.ComponentModifier
import com.story.platform.core.domain.component.ComponentResponse
import com.story.platform.core.domain.component.ComponentStatus
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
class ComponentModifyHandler(
    private val componentModifier: ComponentModifier,

    @IOBound
    private val dispatcher: CoroutineDispatcher,

    @Qualifier(KafkaProducerConfig.DEFAULT_ACK_ALL_KAFKA_TEMPLATE)
    private val kafkaTemplate: KafkaTemplate<String, String>,
) {

    suspend fun patchComponent(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        description: String?,
        status: ComponentStatus?,
    ): ComponentResponse {
        val component = componentModifier.patchComponent(
            workspaceId = workspaceId,
            resourceId = resourceId,
            componentId = componentId,
            description = description,
            status = status,
        )

        withContext(dispatcher) {
            kafkaTemplate.send(
                KafkaTopicFinder.getTopicName(TopicType.COMPONENT),
                "$workspaceId:$resourceId:$componentId",
                ComponentEvent.updated(
                    workspaceId = workspaceId,
                    resourceId = resourceId,
                    componentId = componentId,
                    status = component.status,
                ).toJson()
            )
        }
        return component
    }

}
