package com.story.core.common.model

import java.time.LocalDateTime

data class AuditingTime(
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {

    fun updated(now: LocalDateTime = LocalDateTime.now()) = this.copy(
        updatedAt = now,
    )

    companion object {
        fun created(now: LocalDateTime = LocalDateTime.now()): AuditingTime {
            return AuditingTime(
                createdAt = now,
                updatedAt = now,
            )
        }
    }

}
