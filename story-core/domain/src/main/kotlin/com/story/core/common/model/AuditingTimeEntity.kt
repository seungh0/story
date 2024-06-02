package com.story.core.common.model

import java.time.LocalDateTime

data class AuditingTimeEntity(
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {

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
