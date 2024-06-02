package com.story.core.domain.feed.mapping

import com.story.core.common.model.AuditingTimeEntity
import com.story.core.domain.resource.ResourceId
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Embedded
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Duration

@Table("feed_mapping_v1")
data class FeedMappingEntity(
    @field:PrimaryKey
    val key: FeedMappingPrimaryKey,

    var description: String,
    var retention: Duration,

    @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL)
    var auditingTime: AuditingTimeEntity,
) {

    fun patch(description: String?, retention: Duration?) {
        if (description != null) {
            this.description = description
        }

        if (retention != null) {
            this.retention = retention
        }

        this.auditingTime = this.auditingTime.updated()
    }

    fun toFeedMapping() = FeedMapping(
        workspaceId = this.key.workspaceId,
        feedComponentId = this.key.feedComponentId,
        sourceResourceId = this.key.sourceResourceId,
        sourceComponentId = this.key.sourceComponentId,
        subscriptionComponentId = this.key.subscriptionComponentId,
        retention = this.retention,
    )

    companion object {
        fun of(
            workspaceId: String,
            feedComponentId: String,
            sourceResourceId: ResourceId,
            sourceComponentId: String,
            subscriptionComponentId: String,
            description: String,
            retention: Duration,
        ) = FeedMappingEntity(
            key = FeedMappingPrimaryKey(
                workspaceId = workspaceId,
                feedComponentId = feedComponentId,
                sourceResourceId = sourceResourceId,
                sourceComponentId = sourceComponentId,
                subscriptionComponentId = subscriptionComponentId,
            ),
            description = description,
            retention = retention,
            auditingTime = AuditingTimeEntity.created(),
        )
    }

}

@PrimaryKeyClass
data class FeedMappingPrimaryKey(
    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 1)
    val workspaceId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 2)
    val feedComponentId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 3)
    val sourceResourceId: ResourceId,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 4)
    val sourceComponentId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 5)
    val subscriptionComponentId: String,
)
