package com.story.platform.core.domain.subscription

import com.story.platform.core.common.enums.ServiceType
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType.PARTITIONED
import org.springframework.data.cassandra.core.mapping.CassandraType
import org.springframework.data.cassandra.core.mapping.CassandraType.Name.COUNTER
import org.springframework.data.cassandra.core.mapping.CassandraType.Name.TEXT
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table(SubscriptionTableNames.SUBSCRIPTIONS_COUNTER)
data class SubscriptionsCounter(
    @field:PrimaryKey
    val key: SubscriptionsCounterPrimaryKey,

    @field:Column(value = "count")
    @field:CassandraType(type = COUNTER)
    val count: Long,
)


@PrimaryKeyClass
data class SubscriptionsCounterPrimaryKey(
    @field:PrimaryKeyColumn(value = "service_type", type = PARTITIONED, ordering = Ordering.DESCENDING, ordinal = 1)
    @field:CassandraType(type = TEXT)
    val serviceType: ServiceType,

    @field:PrimaryKeyColumn(
        value = "subscription_type",
        type = PARTITIONED,
        ordering = Ordering.DESCENDING,
        ordinal = 2
    )
    @field:CassandraType(type = TEXT)
    val subscriptionType: SubscriptionType,

    @field:PrimaryKeyColumn(value = "subscriber_id", type = PARTITIONED, ordering = Ordering.DESCENDING, ordinal = 3)
    @field:CassandraType(type = TEXT)
    val subscriberId: String,
)
