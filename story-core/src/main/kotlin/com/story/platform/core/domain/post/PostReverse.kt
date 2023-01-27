package com.story.platform.core.domain.post

import org.springframework.data.cassandra.core.cql.PrimaryKeyType.CLUSTERED
import org.springframework.data.cassandra.core.cql.PrimaryKeyType.PARTITIONED
import org.springframework.data.cassandra.core.mapping.CassandraType
import org.springframework.data.cassandra.core.mapping.CassandraType.Name.BIGINT
import org.springframework.data.cassandra.core.mapping.CassandraType.Name.TEXT
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("post_reverse_v1")
data class PostReverse(
    @field:PrimaryKey
    val key: PostReversePrimaryKey,

    @field:Column(value = "title")
    @field:CassandraType(type = TEXT)
    val title: String,

    @field:Column(value = "content")
    @field:CassandraType(type = TEXT)
    val content: String,

    @field:Column(value = "extra_json")
    @field:CassandraType(type = TEXT)
    val extraJson: String?,
)


@PrimaryKeyClass
data class PostReversePrimaryKey(
    @field:PrimaryKeyColumn(value = "service_type", type = PARTITIONED)
    @field:CassandraType(type = TEXT)
    val serviceType: com.story.platform.core.common.enums.ServiceType,

    @field:PrimaryKeyColumn(value = "account_id", type = PARTITIONED)
    @field:CassandraType(type = TEXT)
    val accountId: String,

    @field:PrimaryKeyColumn(value = "slot_no", type = PARTITIONED)
    @field:CassandraType(type = BIGINT)
    val slotNo: Long,

    @field:PrimaryKeyColumn(value = "space_type", type = CLUSTERED)
    @field:CassandraType(type = TEXT)
    val spaceType: String,

    @field:PrimaryKeyColumn(value = "space_id", type = CLUSTERED)
    @field:CassandraType(type = TEXT)
    val spaceId: String,
)
