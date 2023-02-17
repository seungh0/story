package com.story.platform.core.domain.subscription

import com.story.platform.core.common.enums.ServiceType
import org.springframework.data.cassandra.core.cql.Ordering.ASCENDING
import org.springframework.data.cassandra.core.cql.Ordering.DESCENDING
import org.springframework.data.cassandra.core.cql.PrimaryKeyType.CLUSTERED
import org.springframework.data.cassandra.core.cql.PrimaryKeyType.PARTITIONED
import org.springframework.data.cassandra.core.mapping.CassandraType
import org.springframework.data.cassandra.core.mapping.CassandraType.Name.BIGINT
import org.springframework.data.cassandra.core.mapping.CassandraType.Name.TEXT
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table(SubscriptionTableNames.SUBSCRIPTION)
data class Subscription(
    @field:PrimaryKey
    val key: SubscriptionPrimaryKey,
) {

    companion object {
        fun of(
            serviceType: ServiceType,
            subscriptionType: String,
            targetId: String,
            slotId: Long,
            subscriberId: String,
        ) = Subscription(
            key = SubscriptionPrimaryKey(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                targetId = targetId,
                slotId = slotId,
                subscriberId = subscriberId,
            ),
        )
    }

}


@PrimaryKeyClass
data class SubscriptionPrimaryKey(
    @field:PrimaryKeyColumn(value = "service_type", type = PARTITIONED, ordering = DESCENDING, ordinal = 1)
    @field:CassandraType(type = TEXT)
    val serviceType: ServiceType,

    @field:PrimaryKeyColumn(value = "subscription_type", type = PARTITIONED, ordering = DESCENDING, ordinal = 2)
    @field:CassandraType(type = TEXT)
    val subscriptionType: String,

    @field:PrimaryKeyColumn(value = "target_id", type = PARTITIONED, ordering = DESCENDING, ordinal = 3)
    @field:CassandraType(type = TEXT)
    val targetId: String,

    @field:PrimaryKeyColumn(value = "slot_id", type = PARTITIONED, ordering = ASCENDING, ordinal = 4)
    @field:CassandraType(type = BIGINT)
    val slotId: Long,

    @field:PrimaryKeyColumn(value = "subscriber_id", type = CLUSTERED, ordering = ASCENDING, ordinal = 5)
    @field:CassandraType(type = TEXT)
    val subscriberId: String,
)
