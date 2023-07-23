package com.story.platform.core.common.model

import java.time.LocalDateTime

data class AuditingTime(
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {

    fun updated() = this.copy(
        updatedAt = LocalDateTime.now()
    )

    companion object {
        fun created(): AuditingTime {
            val now = LocalDateTime.now()
            return AuditingTime(
                createdAt = now,
                updatedAt = now,
            )
        }
    }

}
