package com.story.core.domain.subscription

import com.story.core.domain.subscription.SubscriptionTableNames.SUBSCRIBER_COUNT_V1
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.CassandraType
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table(SUBSCRIBER_COUNT_V1)
data class SubscriberCount(
    @field:PrimaryKey
    val key: SubscriberCountPrimaryKey,

    @field:CassandraType(type = CassandraType.Name.COUNTER)
    val count: Long,
)

@PrimaryKeyClass
data class SubscriberCountPrimaryKey(
    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 1)
    val workspaceId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 2)
    val componentId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 3)
    val targetId: String,
)
