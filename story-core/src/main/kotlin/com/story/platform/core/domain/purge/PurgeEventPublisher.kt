package com.story.platform.core.domain.purge

import com.story.platform.core.common.coroutine.IOBound
import com.story.platform.core.common.distribution.DistributionKey
import com.story.platform.core.common.json.toJson
import com.story.platform.core.domain.event.EventHistoryManager
import com.story.platform.core.domain.resource.ResourceId
import com.story.platform.core.infrastructure.kafka.KafkaProducerConfig
import com.story.platform.core.infrastructure.kafka.TopicType
import com.story.platform.core.infrastructure.kafka.send
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class PurgeEventPublisher(
    @Qualifier(KafkaProducerConfig.DEFAULT_ACK_ALL_KAFKA_TEMPLATE)
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val eventHistoryManager: EventHistoryManager,

    @IOBound
    private val dispatcher: CoroutineDispatcher,
) {

    suspend fun publishEvents(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        distributionKeys: Collection<DistributionKey>,
    ) {
        val events = distributionKeys.map { distributionKey ->
            PurgeEvent.created(
                workspaceId = workspaceId,
                resourceId = resourceId,
                componentId = componentId,
                distributionKey = distributionKey,
            )
        }

        eventHistoryManager.withSaveEventHistories(
            workspaceId = workspaceId,
            resourceId = resourceId,
            componentId = componentId,
            events = events,
        ) {
            withContext(dispatcher) {
                events.map { event ->
                    launch {
                        kafkaTemplate.send(topicType = TopicType.PURGE, data = event.toJson())
                    }
                }.joinAll()
            }
        }
    }

}
