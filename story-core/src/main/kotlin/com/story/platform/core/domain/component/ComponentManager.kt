package com.story.platform.core.domain.component

import com.story.platform.core.common.coroutine.IOBound
import com.story.platform.core.common.json.toJson
import com.story.platform.core.domain.resource.ResourceId
import com.story.platform.core.infrastructure.kafka.KafkaProducerConfig
import com.story.platform.core.infrastructure.kafka.KafkaTopicFinder
import com.story.platform.core.infrastructure.kafka.TopicType
import com.story.platform.core.support.cache.CacheEvict
import com.story.platform.core.support.cache.CacheStrategyType
import com.story.platform.core.support.cache.CacheType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class ComponentManager(
    private val componentRepository: ComponentRepository,

    @IOBound
    private val dispatcher: CoroutineDispatcher,

    @Qualifier(KafkaProducerConfig.DEFAULT_ACK_ALL_KAFKA_TEMPLATE)
    private val kafkaTemplate: KafkaTemplate<String, String>,
) {

    suspend fun createComponent(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        description: String,
    ): ComponentResponse {
        val exists = componentRepository.existsById(
            ComponentPrimaryKey(
                workspaceId = workspaceId,
                resourceId = resourceId,
                componentId = componentId,
            )
        )

        if (exists) {
            throw ComponentAlreadyExistsException(message = "워크스페이스($workspaceId)에 이미 등록된 리소스($resourceId) 컴포넌트($componentId)입니다.")
        }

        val component = Component.of(
            workspaceId = workspaceId,
            resourceId = resourceId,
            componentId = componentId,
            description = description,
            status = ComponentStatus.ENABLED
        )
        componentRepository.save(component)

        return ComponentResponse.of(component)
    }

    @CacheEvict(
        cacheType = CacheType.COMPONENT,
        key = "'workspaceId:' + {#workspaceId} + ':resourceId:' + {#resourceId} + ':componentId:' + {#componentId}",
        targetCacheStrategies = [CacheStrategyType.GLOBAL],
    )
    suspend fun patchComponent(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        description: String?,
        status: ComponentStatus?,
    ): ComponentResponse {
        val component = componentRepository.findById(
            ComponentPrimaryKey(
                workspaceId = workspaceId,
                resourceId = resourceId,
                componentId = componentId,
            )
        )
            ?: throw ComponentNotExistsException(message = "워크스페이스($workspaceId)에 등록되지 않은 리소스($resourceId) 컴포넌트($componentId)입니다.")

        component.patch(description = description, status = status)

        componentRepository.save(component)

        withContext(dispatcher) {
            kafkaTemplate.send(
                KafkaTopicFinder.getTopicName(TopicType.COMPONENT),
                "$workspaceId:$resourceId:$component",
                ComponentEvent.updated(component = component).toJson()
            )
        }

        return ComponentResponse.of(component)
    }

}
