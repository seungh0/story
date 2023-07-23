package com.story.platform.core.domain.event

import org.springframework.stereotype.Service

@Service
class EventHistoryManager(
    private val eventHistoryRepository: EventHistoryRepository,
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
            val eventHistory = EventHistory.failed(
                workspaceId = workspaceId,
                componentId = componentId,
                eventRecord = event,
                exception = exception,
            )
            eventHistoryRepository.save(eventHistory)
            throw exception
        }
        val eventHistory = EventHistory.success(
            workspaceId = workspaceId,
            componentId = componentId,
            eventRecord = event,
        )
        eventHistoryRepository.save(eventHistory)
    }

}
