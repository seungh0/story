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
        publishEvent: () -> Unit,
    ) {
        try {
            publishEvent.invoke()
        } catch (exception: Exception) {
            val eventHistory = EventHistory.of(
                workspaceId = workspaceId,
                componentId = componentId,
                eventRecord = event,
                status = EventStatus.ERROR
            )
            reactiveCassandraOperations.batchOps()
                .insert(eventHistory)
                .insert(EventHistoryReverse.of(eventHistory))
                .executeCoroutine()

            throw exception
        }

        val eventHistory = EventHistory.of(
            workspaceId = workspaceId,
            componentId = componentId,
            eventRecord = event,
            status = EventStatus.PUBLISHED
        )
        reactiveCassandraOperations.batchOps()
            .insert(eventHistory)
            .insert(EventHistoryReverse.of(eventHistory))
            .executeCoroutine()
    }

}
