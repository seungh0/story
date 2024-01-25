package com.story.core.domain.event

import com.story.core.domain.resource.ResourceId
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Service

@Service
class EventHistoryManager(
    private val eventHistoryRepository: EventHistoryRepository,
) {

    suspend fun <T> withSaveEventHistories(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        events: Collection<EventRecord<T>>,
        concurrency: Int = 30,
        eventPublisher: suspend (EventRecord<T>) -> Unit,
    ) = coroutineScope {
        events.chunked(concurrency).map { chunkedEvents ->
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
            eventHistoryRepository.save(
                EventHistory.success(
                    workspaceId = workspaceId,
                    resourceId = resourceId,
                    componentId = componentId,
                    eventRecord = event,
                )
            )
        }.onFailure { exception ->
            eventHistoryRepository.save(
                EventHistory.failed(
                    workspaceId = workspaceId,
                    resourceId = resourceId,
                    componentId = componentId,
                    eventRecord = event,
                    exception = exception,
                )
            )
            throw exception
        }
    }

}
