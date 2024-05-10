package com.story.core.domain.subscription

import org.springframework.data.cassandra.core.cql.Ordering.ASCENDING
import org.springframework.data.cassandra.core.cql.Ordering.DESCENDING
import org.springframework.data.cassandra.core.cql.PrimaryKeyType.CLUSTERED
import org.springframework.data.cassandra.core.cql.PrimaryKeyType.PARTITIONED
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("subscription_v1")
data class SubscriptionEntity(
    @field:PrimaryKey
    val key: SubscriptionPrimaryKey,
    var status: SubscriptionStatus,
    val slotId: Long,
    val alarmEnabled: Boolean,
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
            subscriber: SubscriberEntity,
            status: SubscriptionStatus = SubscriptionStatus.ACTIVE,
        ) = SubscriptionEntity(
            key = SubscriptionPrimaryKey.of(
                workspaceId = subscriber.key.workspaceId,
                componentId = subscriber.key.componentId,
                subscriberId = subscriber.key.subscriberId,
                targetId = subscriber.key.targetId,
            ),
            slotId = subscriber.key.slotId,
            status = status,
            alarmEnabled = subscriber.alarmEnabled,
        )
    }

}

@PrimaryKeyClass
data class SubscriptionPrimaryKey(
    @field:PrimaryKeyColumn(type = PARTITIONED, ordinal = 1)
    val workspaceId: String,

    @field:PrimaryKeyColumn(type = PARTITIONED, ordinal = 2)
    val componentId: String,

    @field:PrimaryKeyColumn(type = PARTITIONED, ordinal = 3)
    val distributionKey: String,

    @field:PrimaryKeyColumn(type = CLUSTERED, ordering = DESCENDING, ordinal = 4)
    val subscriberId: String,

    @field:PrimaryKeyColumn(type = CLUSTERED, ordering = ASCENDING, ordinal = 5)
    val targetId: String,
) {

    companion object {
        fun of(
            workspaceId: String,
            componentId: String,
            subscriberId: String,
            targetId: String,
        ) = SubscriptionPrimaryKey(
            workspaceId = workspaceId,
            componentId = componentId,
            distributionKey = SubscriptionDistributionKey.makeKey(subscriberId),
            subscriberId = subscriberId,
            targetId = targetId,
        )
    }

}
