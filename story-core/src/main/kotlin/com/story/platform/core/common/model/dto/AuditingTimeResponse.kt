package com.story.platform.core.common.model.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.story.platform.core.common.model.AuditingTime
import java.time.LocalDateTime

abstract class AuditingTimeResponse {

    @field:JsonProperty
    lateinit var createdAt: LocalDateTime

    @field:JsonProperty
    lateinit var updatedAt: LocalDateTime

    fun from(auditingTime: AuditingTime) {
        this.createdAt = auditingTime.createdAt
        this.updatedAt = auditingTime.updatedAt
    }

    fun from(auditingTimeResponse: AuditingTimeResponse) {
        this.createdAt = auditingTimeResponse.createdAt
        this.updatedAt = auditingTimeResponse.updatedAt
    }

}
