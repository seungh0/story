package com.story.platform.core.domain.subscription

import com.story.platform.core.common.enums.ServiceType
import org.springframework.data.cassandra.core.cql.Ordering.ASCENDING
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

@Table("subscription_reverse_v1")
data class SubscriptionReverse(
    @field:PrimaryKey
    val key: SubscriptionReversePrimaryKey,

    @field:Column("status")
    @field:CassandraType(type = CassandraType.Name.TEXT)
    var status: SubscriptionStatus,

    @field:Column(value = "slot_id")
    @field:CassandraType(type = BIGINT)
    val slotId: Long,
) {

    fun isDeleted(): Boolean {
        return this.status == SubscriptionStatus.DELETED
    }

    fun isActivated(): Boolean {
        return this.status == SubscriptionStatus.ACTIVE
    }

    fun activate() {
        this.status = SubscriptionStatus.ACTIVE
    }

    fun delete() {
        this.status = SubscriptionStatus.DELETED
    }

    companion object {
        fun of(
            subscription: Subscription,
            status: SubscriptionStatus = SubscriptionStatus.ACTIVE,
        ) = SubscriptionReverse(
            key = SubscriptionReversePrimaryKey(
                serviceType = subscription.key.serviceType,
                subscriptionType = subscription.key.subscriptionType,
                subscriberId = subscription.key.subscriberId,
                targetId = subscription.key.targetId,
            ),
            slotId = subscription.key.slotId,
            status = status,
        )
    }

}


@PrimaryKeyClass
data class SubscriptionReversePrimaryKey(
    @field:PrimaryKeyColumn(value = "service_type", type = PARTITIONED, ordering = DESCENDING, ordinal = 1)
    @field:CassandraType(type = TEXT)
    val serviceType: ServiceType,

    @field:PrimaryKeyColumn(value = "subscription_type", type = PARTITIONED, ordering = DESCENDING, ordinal = 2)
    @field:CassandraType(type = TEXT)
    val subscriptionType: String,

    @field:PrimaryKeyColumn(value = "subscriber_id", type = PARTITIONED, ordering = DESCENDING, ordinal = 3)
    @field:CassandraType(type = TEXT)
    val subscriberId: String,

    @field:PrimaryKeyColumn(value = "target_id", type = CLUSTERED, ordering = ASCENDING, ordinal = 4)
    @field:CassandraType(type = TEXT)
    val targetId: String,
)
