package com.story.core.common.model

import java.time.LocalDateTime

interface AuditingTime {

    val createdAt: LocalDateTime
    val updatedAt: LocalDateTime

}
