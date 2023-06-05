package com.story.platform.core.domain.feed

import com.story.platform.core.common.enums.ServiceType
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.CassandraType
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import java.time.LocalDateTime

data class FeedbackReverse(
    @field:PrimaryKeyColumn(
        value = "created_at",
        type = PrimaryKeyType.CLUSTERED,
        ordering = Ordering.DESCENDING,
        ordinal = 3,
    )
    @field:CassandraType(type = CassandraType.Name.TIMESTAMP)
    val createdAt: LocalDateTime,
)

data class FeedbackReversePrimaryKey(
    @field:PrimaryKeyColumn(value = "service_type", type = PrimaryKeyType.PARTITIONED, ordinal = 1)
    @field:CassandraType(type = CassandraType.Name.TEXT)
    val serviceType: ServiceType,

    @field:PrimaryKeyColumn(value = "account_id", type = PrimaryKeyType.PARTITIONED, ordinal = 2)
    @field:CassandraType(type = CassandraType.Name.TEXT)
    val accountId: String,

    @field:PrimaryKeyColumn(
        value = "feedback_type",
        type = PrimaryKeyType.PARTITIONED,
        ordinal = 3,
    )
    @field:CassandraType(type = CassandraType.Name.TEXT)
    val feedType: FeedType,

    @field:PrimaryKeyColumn(
        value = "feedback_id",
        type = PrimaryKeyType.CLUSTERED,
        ordering = Ordering.DESCENDING,
        ordinal = 4,
    )
    @field:CassandraType(type = CassandraType.Name.TEXT)
    val feedbackId: String,
)
