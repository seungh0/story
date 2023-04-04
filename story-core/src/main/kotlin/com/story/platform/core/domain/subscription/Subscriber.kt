package com.story.platform.core.domain.subscription

import com.story.platform.core.common.enums.ServiceType
import org.springframework.data.cassandra.core.cql.Ordering.ASCENDING
import org.springframework.data.cassandra.core.cql.PrimaryKeyType.CLUSTERED
import org.springframework.data.cassandra.core.cql.PrimaryKeyType.PARTITIONED
import org.springframework.data.cassandra.core.mapping.CassandraType
import org.springframework.data.cassandra.core.mapping.CassandraType.Name.BIGINT
import org.springframework.data.cassandra.core.mapping.CassandraType.Name.TEXT
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table(SubscriptionTableNames.SUBSCRIBER)
data class Subscriber(
    @field:PrimaryKey
    val key: SubscriberPrimaryKey,
) {

    companion object {
        fun of(
            serviceType: ServiceType,
            subscriptionType: SubscriptionType,
            targetId: String,
            slotId: Long,
            subscriberId: String,
        ) = Subscriber(
            key = SubscriberPrimaryKey(
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
data class SubscriberPrimaryKey(
    @field:PrimaryKeyColumn(value = "service_type", type = PARTITIONED, ordinal = 1)
    @field:CassandraType(type = TEXT)
    val serviceType: ServiceType,

    @field:PrimaryKeyColumn(value = "subscription_type", type = PARTITIONED, ordinal = 2)
    @field:CassandraType(type = TEXT)
    val subscriptionType: SubscriptionType,

    @field:PrimaryKeyColumn(value = "target_id", type = PARTITIONED, ordinal = 3)
    @field:CassandraType(type = TEXT)
    val targetId: String,

    @field:PrimaryKeyColumn(value = "slot_id", type = PARTITIONED, ordinal = 4)
    @field:CassandraType(type = BIGINT)
    val slotId: Long,

    @field:PrimaryKeyColumn(value = "subscriber_id", type = CLUSTERED, ordering = ASCENDING, ordinal = 5)
    @field:CassandraType(type = TEXT)
    val subscriberId: String,
)
