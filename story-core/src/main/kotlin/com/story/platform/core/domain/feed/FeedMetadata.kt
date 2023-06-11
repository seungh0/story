package com.story.platform.core.domain.feed

import com.story.platform.core.common.enums.ServiceType
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("feed_metadata_v1")
data class FeedMetadata(
    @field:PrimaryKey
    val key: FeedMetadataPrimaryKey,

    val slotCount: Long,
)

@PrimaryKeyClass
data class FeedMetadataPrimaryKey(
    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 1)
    val serviceType: ServiceType,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 2)
    val sourceType: FeedSourceType,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 3)
    val sourceId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 4)
    val feedId: String,
)
