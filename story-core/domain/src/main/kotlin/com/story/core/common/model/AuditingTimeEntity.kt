package com.story.core.common.model

import java.time.LocalDateTime

data class AuditingTimeEntity(
    override val createdAt: LocalDateTime,
    override val updatedAt: LocalDateTime,
) : AuditingTime {

    fun updated(now: LocalDateTime = LocalDateTime.now()) = this.copy(
        updatedAt = now,
    )

    companion object {
        fun created(now: LocalDateTime = LocalDateTime.now()): AuditingTimeEntity {
            return AuditingTimeEntity(
                createdAt = now,
                updatedAt = now,
            )
        }
    }

}
