package com.story.platform.core.domain.event

import com.story.platform.core.domain.resource.ResourceId
import com.story.platform.core.infrastructure.cassandra.executeCoroutine
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.stereotype.Service

@Service
class EventHistoryManager(
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
) {

    suspend fun <T> withSaveEventHistory(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        event: EventRecord<T>,
        eventPublisher: suspend (EventRecord<T>) -> Unit,
    ) {
        try {
            eventPublisher.invoke(event)
        } catch (exception: Exception) {
            reactiveCassandraOperations.batchOps()
                .insert(
                    EventHistory.failed(
                        workspaceId = workspaceId,
                        resourceId = resourceId,
                        componentId = componentId,
                        eventRecord = event,
                        exception = exception,
                    )
                )
                .insert(
                    EventKeyIdMapping.of(
                        workspaceId = workspaceId,
                        eventKey = event.eventKey,
                        eventId = event.eventId,
                    )
                )
                .executeCoroutine()
            throw exception
        }

        reactiveCassandraOperations.batchOps()
            .insert(
                EventHistory.success(
                    workspaceId = workspaceId,
                    resourceId = resourceId,
                    componentId = componentId,
                    eventRecord = event,
                )
            )
            .insert(
                EventKeyIdMapping.of(
                    workspaceId = workspaceId,
                    eventKey = event.eventKey,
                    eventId = event.eventId,
                )
            )
            .executeCoroutine()
    }

}
