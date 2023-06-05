package com.story.platform.core.common.model

import com.story.platform.core.common.enums.EventType
import com.story.platform.core.common.utils.EventIdGenerator
import java.time.LocalDateTime

data class EventRecord<T>(
    val payload: T,
    val eventType: EventType,
    val eventId: String = EventIdGenerator.generate(),
    val timestamp: LocalDateTime = LocalDateTime.now(),
)
