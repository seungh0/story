package com.story.platform.core.domain.event

import com.story.platform.core.infrastructure.cassandra.executeCoroutine
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.stereotype.Service

@Service
class EventHistoryManager(
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
) {

    suspend fun <T> withSaveEventHistory(
        workspaceId: String,
        componentId: String,
        event: EventRecord<T>,
        eventPublisher: suspend () -> Unit,
    ) {
        try {
            eventPublisher.invoke()
        } catch (exception: Exception) {
            reactiveCassandraOperations.batchOps()
                .insert(
                    EventHistory.failed(
                        workspaceId = workspaceId,
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
