package com.story.core.common.model.dto

import com.story.core.common.model.AuditingTime
import java.time.LocalDateTime

abstract class AuditingTimeResponse : AuditingTime {

    override lateinit var createdAt: LocalDateTime
    override lateinit var updatedAt: LocalDateTime

    fun setAuditingTime(auditingTime: AuditingTime) {
        this.createdAt = auditingTime.createdAt
        this.updatedAt = auditingTime.updatedAt
    }

}
