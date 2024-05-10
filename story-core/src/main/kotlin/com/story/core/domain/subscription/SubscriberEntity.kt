package com.story.core.domain.subscription

import org.springframework.data.cassandra.core.cql.Ordering.ASCENDING
import org.springframework.data.cassandra.core.cql.PrimaryKeyType.CLUSTERED
import org.springframework.data.cassandra.core.cql.PrimaryKeyType.PARTITIONED
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("subscriber_v1")
data class SubscriberEntity(
    @field:PrimaryKey
    val key: SubscriberPrimaryKey,

    val alarmEnabled: Boolean,
) {

    companion object {
        fun of(
            workspaceId: String,
            componentId: String,
            targetId: String,
            slotId: Long,
            subscriberId: String,
            alarm: Boolean,
        ) = SubscriberEntity(
            key = SubscriberPrimaryKey(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                slotId = slotId,
                subscriberId = subscriberId,
            ),
            alarmEnabled = alarm,
        )
    }

}

@PrimaryKeyClass
data class SubscriberPrimaryKey(
    @field:PrimaryKeyColumn(type = PARTITIONED, ordinal = 1)
    val workspaceId: String,

    @field:PrimaryKeyColumn(type = PARTITIONED, ordinal = 2)
    val componentId: String,

    @field:PrimaryKeyColumn(type = PARTITIONED, ordinal = 3)
    val targetId: String,

    @field:PrimaryKeyColumn(type = PARTITIONED, ordinal = 4)
    val slotId: Long,

    @field:PrimaryKeyColumn(type = CLUSTERED, ordering = ASCENDING, ordinal = 5)
    val subscriberId: String,
)
