package com.story.platform.core.domain.reaction

import com.story.platform.core.common.annotation.EventProducer
import com.story.platform.core.common.annotation.IOBound
import com.story.platform.core.common.json.toJson
import com.story.platform.core.domain.event.EventHistoryManager
import com.story.platform.core.domain.event.EventRecord
import com.story.platform.core.domain.resource.ResourceId
import com.story.platform.core.infrastructure.kafka.KafkaProducerConfig
import com.story.platform.core.infrastructure.kafka.KafkaTopic
import com.story.platform.core.infrastructure.kafka.send
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.kafka.core.KafkaTemplate

@EventProducer
class ReactionEventProducer(
    @Qualifier(KafkaProducerConfig.REACTION_KAFKA_PRODUCER)
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val eventHistoryManager: EventHistoryManager,

    @IOBound
    private val dispatcher: CoroutineDispatcher,
) {

    suspend fun publishEvent(change: ReactionChangeResponse) {
        if (change.createdOptionIds.isEmpty() && change.deletedOptionIds.isEmpty()) {
            return
        }
        val event = createEvent(reaction = change)
        eventHistoryManager.withSaveEventHistory(
            workspaceId = change.workspaceId,
            resourceId = ResourceId.REACTIONS,
            componentId = change.componentId,
            event = event,
        ) {
            withContext(dispatcher) {
                kafkaTemplate.send(
                    kafkaTopic = KafkaTopic.REACTION,
                    data = event.toJson()
                )
            }
        }
    }

    private fun createEvent(reaction: ReactionChangeResponse): EventRecord<ReactionEvent> {
        if (reaction.deletedOptionIds.isEmpty()) {
            return ReactionEvent.created(
                workspaceId = reaction.workspaceId,
                componentId = reaction.componentId,
                spaceId = reaction.spaceId,
                accountId = reaction.accountId,
                createdOptionIds = reaction.createdOptionIds,
            )
        }
        if (reaction.createdOptionIds.isEmpty()) {
            return ReactionEvent.deleted(
                workspaceId = reaction.workspaceId,
                componentId = reaction.componentId,
                spaceId = reaction.spaceId,
                accountId = reaction.accountId,
                deletedOptionIds = reaction.deletedOptionIds,
            )
        }

        return ReactionEvent.updated(
            workspaceId = reaction.workspaceId,
            componentId = reaction.componentId,
            spaceId = reaction.spaceId,
            accountId = reaction.accountId,
            createdOptionIds = reaction.createdOptionIds,
            deletedOptionIds = reaction.deletedOptionIds,
        )
    }

}
