package com.story.platform.core.domain.subscription

import com.story.platform.core.common.distribution.LargeDistributionKey
import com.story.platform.core.common.enums.ServiceType
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.CassandraType
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table(SubscriptionTableNames.SUBSCRIPTION_DISTRIBUTED)
data class SubscriptionDistributed(
    @field:PrimaryKey
    val key: SubscriptionDistributedPrimaryKey,
) {
    companion object {
        fun of(subscription: Subscription) = SubscriptionDistributed(
            key = SubscriptionDistributedPrimaryKey.of(
                serviceType = subscription.key.serviceType,
                subscriptionType = subscription.key.subscriptionType,
                targetId = subscription.key.targetId,
                subscriberId = subscription.key.subscriberId,
            )
        )
    }
}

@PrimaryKeyClass
data class SubscriptionDistributedPrimaryKey(
    @field:PrimaryKeyColumn(value = "service_type", type = PrimaryKeyType.PARTITIONED, ordinal = 1)
    @field:CassandraType(type = CassandraType.Name.TEXT)
    val serviceType: ServiceType,

    @field:PrimaryKeyColumn(value = "subscription_type", type = PrimaryKeyType.PARTITIONED, ordinal = 2)
    @field:CassandraType(type = CassandraType.Name.TEXT)
    val subscriptionType: SubscriptionType,

    @field:PrimaryKeyColumn(value = "distributed_key", type = PrimaryKeyType.PARTITIONED, ordinal = 3)
    @field:CassandraType(type = CassandraType.Name.TEXT)
    val distributedKey: String,

    @field:PrimaryKeyColumn(
        value = "target_id",
        type = PrimaryKeyType.CLUSTERED,
        ordering = Ordering.ASCENDING,
        ordinal = 4
    )
    @field:CassandraType(type = CassandraType.Name.TEXT)
    val targetId: String,

    @field:PrimaryKeyColumn(
        value = "subscriber_id",
        type = PrimaryKeyType.CLUSTERED,
        ordering = Ordering.ASCENDING,
        ordinal = 5,
    )
    @field:CassandraType(type = CassandraType.Name.TEXT)
    val subscriberId: String,
) {

    companion object {
        fun of(
            serviceType: ServiceType,
            subscriptionType: SubscriptionType,
            targetId: String,
            subscriberId: String,
        ) = SubscriptionDistributedPrimaryKey(
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            targetId = targetId,
            subscriberId = subscriberId,
            distributedKey = LargeDistributionKey.fromId(rawId = subscriberId).key,
        )
    }

}
