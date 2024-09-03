package com.story.core.domain.feed

import com.story.core.domain.resource.ResourceId
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("feed_reverse_v2")
data class FeedReverseEntity(
    @field:PrimaryKey
    val key: FeedReverseEntityPrimaryKey,
    val sortKey: Long,
) {

    companion object {
        fun from(feed: FeedEntity) = FeedReverseEntity(
            key = FeedReverseEntityPrimaryKey(
                workspaceId = feed.key.workspaceId,
                componentId = feed.key.componentId,
                ownerId = feed.key.ownerId,
                channelId = feed.key.channelId,
                itemResourceId = feed.key.itemResourceId,
                itemComponentId = feed.key.itemComponentId,
                itemId = feed.key.itemId,
            ),
            sortKey = feed.key.sortKey,
        )
    }

}

@PrimaryKeyClass
data class FeedReverseEntityPrimaryKey(
    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 1)
    val workspaceId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 2)
    val componentId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 3)
    val ownerId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 4)
    val channelId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 5)
    val itemResourceId: ResourceId,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 6)
    val itemComponentId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 7)
    val itemId: String,
)
