package com.story.core.domain.feed

import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

/**
 * - 100 (partition) * 100,000 (10 MB /100byte) = 한 피드에 대한 발송 대상자는 10,000,000
 */
@Table("feed_subscriber_v1")
data class FeedSubscriber(
    @field:PrimaryKey
    val key: FeedSubscriberPrimaryKey,

    val feedId: Long,
)

@PrimaryKeyClass
data class FeedSubscriberPrimaryKey(
    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 1)
    val workspaceId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 2)
    val feedComponentId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 3)
    val eventKey: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 4)
    val slotId: Long,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 5)
    val subscriberId: String,
) {

    companion object {
        fun of(
            workspaceId: String,
            feedComponentId: String,
            eventKey: String,
            slotId: Long,
            subscriberId: String,
        ) = FeedSubscriberPrimaryKey(
            workspaceId = workspaceId,
            feedComponentId = feedComponentId,
            eventKey = eventKey,
            slotId = slotId,
            subscriberId = subscriberId,
        )
    }

}