package com.story.platform.core.domain.feed

import com.story.platform.core.common.enums.ServiceType
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.CassandraType
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.LocalDateTime

@Table("feed_reverse_v1")
data class FeedReverse(
    @field:PrimaryKey
    val key: FeedReversePrimaryKey,

    @field:CassandraType(type = CassandraType.Name.TIMESTAMP)
    val createdAt: LocalDateTime,
)

data class FeedReversePrimaryKey(
    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 1)
    @field:CassandraType(type = CassandraType.Name.TEXT)
    val serviceType: ServiceType,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 2)
    @field:CassandraType(type = CassandraType.Name.TEXT)
    val accountId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 3)
    @field:CassandraType(type = CassandraType.Name.TEXT)
    val feedType: FeedType,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 4)
    @field:CassandraType(type = CassandraType.Name.TEXT)
    val feedId: String,
)
