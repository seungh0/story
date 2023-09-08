package com.story.platform.core.domain.event

import com.story.platform.core.domain.resource.ResourceId
import com.story.platform.core.infrastructure.cassandra.executeCoroutine
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.stereotype.Service

@Service
class EventHistoryManager(
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
) {

    suspend fun <T> withSaveEventHistories(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        events: Collection<EventRecord<T>>,
        parallelCount: Int = 30,
        eventPublisher: suspend (EventRecord<T>) -> Unit,
    ) = coroutineScope {
        events.chunked(parallelCount).map { chunkedEvents ->
            chunkedEvents.map { event ->
                async {
                    withSaveEventHistory(
                        workspaceId = workspaceId,
                        resourceId = resourceId,
                        componentId = componentId,
                        event = event,
                        eventPublisher = eventPublisher,
                    )
                }
            }.awaitAll()
        }
    }

    suspend fun <T> withSaveEventHistory(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        event: EventRecord<T>,
        eventPublisher: suspend (EventRecord<T>) -> Unit,
    ) {
        runCatching {
            eventPublisher.invoke(event)
        }.onSuccess {
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
        }.onFailure { exception ->
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
    }

}
