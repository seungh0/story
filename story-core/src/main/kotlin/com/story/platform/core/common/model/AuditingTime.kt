package com.story.platform.core.common.model

import org.springframework.data.cassandra.core.mapping.CassandraType
import java.time.LocalDateTime

data class AuditingTime(
    @field:CassandraType(type = CassandraType.Name.TIMESTAMP)
    val createdAt: LocalDateTime,

    @field:CassandraType(type = CassandraType.Name.TIMESTAMP)
    val updatedAt: LocalDateTime,
) {

    fun updated() = this.copy(
        updatedAt = LocalDateTime.now()
    )

    companion object {
        fun newEntity(): AuditingTime {
            val now = LocalDateTime.now()
            return AuditingTime(
                createdAt = now,
                updatedAt = now,
            )
        }
    }

}
