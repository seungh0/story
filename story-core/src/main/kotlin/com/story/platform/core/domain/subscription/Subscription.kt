package com.story.platform.core.domain.subscription

import com.story.platform.core.common.enums.ServiceType
import org.springframework.data.cassandra.core.cql.Ordering.ASCENDING
import org.springframework.data.cassandra.core.cql.PrimaryKeyType.CLUSTERED
import org.springframework.data.cassandra.core.cql.PrimaryKeyType.PARTITIONED
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("subscription_v1")
data class Subscription(
    @field:PrimaryKey
    val key: SubscriptionPrimaryKey,

    var status: SubscriptionStatus,

    val slotId: Long,

    val alarm: Boolean,
) {

    fun isDeleted(): Boolean {
        return this.status == SubscriptionStatus.DELETED
    }

    fun isActivated(): Boolean {
        return this.status == SubscriptionStatus.ACTIVE
    }

    fun delete() {
        this.status = SubscriptionStatus.DELETED
    }

    companion object {
        fun of(
            subscriber: Subscriber,
            status: SubscriptionStatus = SubscriptionStatus.ACTIVE,
        ) = Subscription(
            key = SubscriptionPrimaryKey(
                serviceType = subscriber.key.serviceType,
                subscriptionType = subscriber.key.subscriptionType,
                subscriberId = subscriber.key.subscriberId,
                targetId = subscriber.key.targetId,
            ),
            slotId = subscriber.key.slotId,
            status = status,
            alarm = subscriber.alarm,
        )
    }

}

@PrimaryKeyClass
data class SubscriptionPrimaryKey(
    @field:PrimaryKeyColumn(type = PARTITIONED, ordinal = 1)
    val serviceType: ServiceType,

    @field:PrimaryKeyColumn(type = PARTITIONED, ordinal = 2)
    val subscriptionType: SubscriptionType,

    @field:PrimaryKeyColumn(type = PARTITIONED, ordinal = 3)
    val subscriberId: String,

    @field:PrimaryKeyColumn(type = CLUSTERED, ordering = ASCENDING, ordinal = 4)
    val targetId: String,
)
