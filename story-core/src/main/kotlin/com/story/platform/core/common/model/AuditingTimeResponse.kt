package com.story.platform.core.common.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

abstract class AuditingTimeResponse {

    @field:JsonProperty
    private lateinit var createdAt: LocalDateTime

    @field:JsonProperty
    private lateinit var updatedAt: LocalDateTime

    fun from(auditingTime: AuditingTime) {
        this.createdAt = auditingTime.createdAt
        this.updatedAt = auditingTime.updatedAt
    }

}
