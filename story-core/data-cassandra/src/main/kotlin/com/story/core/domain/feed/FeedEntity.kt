package com.story.core.domain.feed

import com.story.core.domain.resource.ResourceId
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.LocalDateTime

@Table("feed_v2")
data class FeedEntity(
    @field:PrimaryKey
    val key: FeedEntityPrimaryKey,
    val createdAt: LocalDateTime,
) {

    fun toFeed() = Feed(
        workspaceId = key.workspaceId,
        componentId = key.componentId,
        ownerId = key.ownerId,
        item = FeedItem(
            itemId = key.itemId,
            componentId = key.itemComponentId,
            resourceId = key.itemResourceId,
            channelId = key.channelId,
        ),
        sortKey = key.sortKey,
        createdAt = createdAt,
    )

    companion object {
        fun from(feedReverse: FeedReverseEntity) = FeedEntity(
            key = FeedEntityPrimaryKey(
                workspaceId = feedReverse.key.workspaceId,
                componentId = feedReverse.key.componentId,
                ownerId = feedReverse.key.ownerId,
                sortKey = feedReverse.sortKey,
                channelId = feedReverse.key.channelId,
                itemResourceId = feedReverse.key.itemResourceId,
                itemComponentId = feedReverse.key.itemComponentId,
                itemId = feedReverse.key.itemId,
            ),
            createdAt = LocalDateTime.now(),
        )
    }

}

@PrimaryKeyClass
data class FeedEntityPrimaryKey(
    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 1)
    val workspaceId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 2)
    val componentId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 3)
    val ownerId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 4)
    val sortKey: Long,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 5)
    val channelId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 6)
    val itemResourceId: ResourceId,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 7)
    val itemComponentId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 8)
    val itemId: String,
)
