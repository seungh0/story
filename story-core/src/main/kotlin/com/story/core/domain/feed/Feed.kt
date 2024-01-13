package com.story.core.domain.feed

import com.story.core.domain.resource.ResourceId
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

/**
 * 유저 당 100,000개의 피드 내로 되어야 최적이 성능을 보장함 (최대 1,000,000)
 * - 100,000 (10 MB /100byte)
 */
@Table("feed_v1")
data class Feed(
    @field:PrimaryKey
    val key: FeedPrimaryKey,
    val eventKey: String,
    val subscriberSlot: Long,
    val sourceResourceId: ResourceId,
    val sourceComponentId: String,
) {

    companion object {
        fun from(feedSubscriber: FeedSubscriber) = Feed(
            key = FeedPrimaryKey(
                workspaceId = feedSubscriber.key.workspaceId,
                feedComponentId = feedSubscriber.key.feedComponentId,
                subscriberId = feedSubscriber.key.subscriberId,
                feedId = feedSubscriber.feedId,
            ),
            subscriberSlot = feedSubscriber.key.slotId,
            eventKey = feedSubscriber.key.eventKey,
            sourceResourceId = feedSubscriber.sourceResourceId,
            sourceComponentId = feedSubscriber.sourceComponentId,
        )
    }

}

@PrimaryKeyClass
data class FeedPrimaryKey(
    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 1)
    val workspaceId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 2)
    val feedComponentId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 4)
    val subscriberId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 5)
    val feedId: Long,
) {

    companion object {
        fun of(
            workspaceId: String,
            feedComponentId: String,
            subscriberId: String,
            feedId: Long,
        ) = FeedPrimaryKey(
            workspaceId = workspaceId,
            feedComponentId = feedComponentId,
            subscriberId = subscriberId,
            feedId = feedId,
        )
    }

}
