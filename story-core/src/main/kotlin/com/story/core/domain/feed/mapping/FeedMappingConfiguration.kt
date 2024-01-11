package com.story.core.domain.feed.mapping

import com.story.core.common.model.AuditingTime
import com.story.core.domain.resource.ResourceId
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Embedded
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("feed_mapping_configuration_v1")
data class FeedMappingConfiguration(
    @field:PrimaryKey
    val key: FeedMappingConfigurationPrimaryKey,

    var description: String,
    var status: FeedMappingConfigurationStatus,

    @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL)
    var auditingTime: AuditingTime,
) {

    fun patch(description: String?) {
        if (description != null) {
            this.description = description
        }

        this.auditingTime = this.auditingTime.updated()
    }

    fun disconnect() {
        this.status = FeedMappingConfigurationStatus.DISABLED
        this.auditingTime = this.auditingTime.updated()
    }

    companion object {
        fun of(
            workspaceId: String,
            feedComponentId: String,
            sourceResourceId: ResourceId,
            sourceComponentId: String,
            subscriptionComponentId: String,
            description: String,
            status: FeedMappingConfigurationStatus = FeedMappingConfigurationStatus.ENABLED,
        ) = FeedMappingConfiguration(
            key = FeedMappingConfigurationPrimaryKey(
                workspaceId = workspaceId,
                feedComponentId = feedComponentId,
                sourceResourceId = sourceResourceId,
                sourceComponentId = sourceComponentId,
                subscriptionComponentId = subscriptionComponentId,
            ),
            description = description,
            auditingTime = AuditingTime.created(),
            status = status,
        )
    }

}

@PrimaryKeyClass
data class FeedMappingConfigurationPrimaryKey(
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
