package com.story.datacenter.core.domain.subscription

import com.story.datacenter.core.common.enums.ServiceType
import org.springframework.data.cassandra.core.cql.Ordering.DESCENDING
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

@Table("subscription_v1")
data class Subscription(
    @field:PrimaryKey
    val key: SubscriptionPrimaryKey,

    @field:Column(value = "extra_json")
    @field:CassandraType(type = TEXT)
    val extraJson: String? = null,
) {

    companion object {
        fun of(
            serviceType: ServiceType,
            subscriptionType: String,
            targetId: String,
            partitionId: Long,
            subscriberId: String,
        ) = Subscription(
            key = SubscriptionPrimaryKey(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                targetId = targetId,
                slotNo = partitionId,
                subscriberId = subscriberId,
            ),
        )
    }

}


@PrimaryKeyClass
data class SubscriptionPrimaryKey(
    @field:PrimaryKeyColumn(value = "service_type", type = PARTITIONED)
    @field:CassandraType(type = TEXT)
    val serviceType: ServiceType,

    @field:PrimaryKeyColumn(value = "subscription_type", type = PARTITIONED)
    @field:CassandraType(type = TEXT)
    val subscriptionType: String,

    @field:PrimaryKeyColumn(value = "target_id", type = PARTITIONED)
    @field:CassandraType(type = TEXT)
    val targetId: String,

    @field:PrimaryKeyColumn(value = "slot_no", type = PARTITIONED)
    @field:CassandraType(type = BIGINT)
    val slotNo: Long,

    @field:PrimaryKeyColumn(value = "subscriber_id", type = CLUSTERED, ordering = DESCENDING)
    @field:CassandraType(type = TEXT)
    val subscriberId: String,
)
