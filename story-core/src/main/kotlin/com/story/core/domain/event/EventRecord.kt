package com.story.core.domain.event

import java.time.LocalDateTime

data class EventRecord<T>(
    val eventAction: EventAction,
    val payload: T,
    val eventKey: String,
    val eventId: Long = EventIdHelper.generate(),
    val timestamp: LocalDateTime = LocalDateTime.now(),
)
