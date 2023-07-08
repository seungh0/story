package com.story.platform.core.domain.feed

import com.story.platform.core.common.model.AuditingTime
import com.story.platform.core.domain.event.EventAction
import com.story.platform.core.domain.resource.ResourceId
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

    val description: String = "",
    var extraJson: String?,

    @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL)
    val auditingTime: AuditingTime,
)

@PrimaryKeyClass
data class FeedMappingConfigurationPrimaryKey(
    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 1)
    val workspaceId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 2)
    val resourceId: ResourceId,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 3)
    val componentId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 4)
    val eventAction: EventAction,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 5)
    val subscriptionComponentId: String,
)
