package com.story.datacenter.core.common.model

import org.springframework.data.cassandra.core.mapping.CassandraType
import org.springframework.data.cassandra.core.mapping.Column
import java.time.LocalDateTime

data class AuditingTime(
    @field:Column("created_at")
    @field:CassandraType(type = CassandraType.Name.TIMESTAMP)
    val createdAt: LocalDateTime,

    @field:Column("updated_at")
    @field:CassandraType(type = CassandraType.Name.TIMESTAMP)
    val updatedAt: LocalDateTime,
)
