package com.story.platform.core.domain.event

import com.story.platform.core.domain.resource.ResourceId
import java.time.LocalDateTime

data class EventRecord<T>(
    val resourceId: ResourceId,
    val eventAction: EventAction,
    val payload: T,
    val eventId: String = EventIdGenerator.generate(),
    val timestamp: LocalDateTime = LocalDateTime.now(),
)
