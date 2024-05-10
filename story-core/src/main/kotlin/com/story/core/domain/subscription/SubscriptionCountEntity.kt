package com.story.core.domain.subscription

import com.story.core.domain.subscription.SubscriptionTableNames.SUBSCRIPTION_COUNT_V1
import com.story.core.infrastructure.cassandra.CassandraEntity
import com.story.core.infrastructure.cassandra.CassandraKey
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.CassandraType
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table(SUBSCRIPTION_COUNT_V1)
data class SubscriptionCountEntity(
    @field:PrimaryKey
    override val key: SubscriptionCountPrimaryKey,

    @field:CassandraType(type = CassandraType.Name.COUNTER)
    val count: Long,
) : CassandraEntity

@PrimaryKeyClass
data class SubscriptionCountPrimaryKey(
    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 1)
    val workspaceId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 2)
    val componentId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 3)
    val subscriberId: String,
) : CassandraKey
