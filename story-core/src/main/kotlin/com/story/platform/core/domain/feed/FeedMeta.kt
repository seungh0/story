package com.story.platform.core.domain.feed

import com.story.platform.core.common.enums.ServiceType
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.CassandraType
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("feed_meta_v1")
data class FeedMeta(
    @field:PrimaryKey
    val key: FeedMetaPrimaryKey,

    @field:CassandraType(type = CassandraType.Name.BIGINT)
    val slotCount: Long,
)

@PrimaryKeyClass
data class FeedMetaPrimaryKey(
    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 1)
    @field:CassandraType(type = CassandraType.Name.TEXT)
    val serviceType: ServiceType,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 2)
    @field:CassandraType(type = CassandraType.Name.TEXT)
    val targetId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 3)
    @field:CassandraType(type = CassandraType.Name.TEXT)
    val feedId: String,
)
