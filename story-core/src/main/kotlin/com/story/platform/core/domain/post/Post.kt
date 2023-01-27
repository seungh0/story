package com.story.platform.core.domain.post

import com.story.platform.core.common.enums.ServiceType
import org.springframework.data.cassandra.core.cql.PrimaryKeyType.CLUSTERED
import org.springframework.data.cassandra.core.cql.PrimaryKeyType.PARTITIONED
import org.springframework.data.cassandra.core.mapping.*
import org.springframework.data.cassandra.core.mapping.CassandraType.Name.TEXT

@Table("post_v1")
data class Post(
    @field:PrimaryKey
    val key: PostPrimaryKey,

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
data class PostPrimaryKey(
    @field:PrimaryKeyColumn(value = "service_type", type = PARTITIONED)
    @field:CassandraType(type = TEXT)
    val serviceType: ServiceType,

    @field:PrimaryKeyColumn(value = "space_type", type = PARTITIONED)
    @field:CassandraType(type = TEXT)
    val spaceType: String,

    @field:PrimaryKeyColumn(value = "space_id", type = PARTITIONED)
    @field:CassandraType(type = TEXT)
    val spaceId: String,

    @field:PrimaryKeyColumn(value = "account_id", type = CLUSTERED)
    @field:CassandraType(type = TEXT)
    val accountId: String,
)
