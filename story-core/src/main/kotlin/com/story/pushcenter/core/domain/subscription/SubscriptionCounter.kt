package com.story.pushcenter.core.domain.subscription

import com.story.pushcenter.core.common.enums.ServiceType
import org.springframework.data.cassandra.core.cql.PrimaryKeyType.PARTITIONED
import org.springframework.data.cassandra.core.mapping.CassandraType
import org.springframework.data.cassandra.core.mapping.CassandraType.Name.COUNTER
import org.springframework.data.cassandra.core.mapping.CassandraType.Name.TEXT
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("subscription_counter")
data class SubscriptionCounter(
    @PrimaryKey
    val key: SubscriptionReversePrimaryKey,

    @field:Column(value = "count")
    @field:CassandraType(type = COUNTER)
    val count: Long,
)


@PrimaryKeyClass
data class SubscriptionCounterPrimaryKey(
    @field:PrimaryKeyColumn(value = "service_type", type = PARTITIONED)
    @field:CassandraType(type = TEXT)
    val serviceType: ServiceType,

    @field:PrimaryKeyColumn(value = "subscription_type", type = PARTITIONED)
    @field:CassandraType(type = TEXT)
    val subscriptionType: String,

    @field:PrimaryKeyColumn(value = "target_id", type = PARTITIONED)
    @field:CassandraType(type = TEXT)
    val targetId: String,
)
