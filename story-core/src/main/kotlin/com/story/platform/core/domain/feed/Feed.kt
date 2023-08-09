package com.story.platform.core.domain.feed

import com.story.platform.core.common.distribution.XLargeDistributionKey
import com.story.platform.core.domain.event.EventAction
import com.story.platform.core.domain.resource.ResourceId
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("feed_v1")
data class Feed(
    @field:PrimaryKey
    val key: FeedPrimaryKey,
    val sourceResourceId: ResourceId,
    val sourceComponentId: String,
    val eventAction: EventAction,
    val payloadJson: String,
)

@PrimaryKeyClass
data class FeedPrimaryKey(
    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 1)
    val workspaceId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 2)
    val feedComponentId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 3)
    val distributionKey: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 4)
    val targetId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 5)
    val eventId: Long,
) {

    companion object {
        fun of(
            workspaceId: String,
            feedComponentId: String,
            targetId: String,
            eventId: Long,
        ) = FeedPrimaryKey(
            workspaceId = workspaceId,
            feedComponentId = feedComponentId,
            distributionKey = XLargeDistributionKey.makeKey(targetId).key,
            targetId = targetId,
            eventId = eventId,
        )
    }

}
