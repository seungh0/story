package com.story.platform.core.domain.feed.configuration

import com.story.platform.core.domain.event.EventAction
import com.story.platform.core.domain.resource.ResourceId
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("feed_reverse_mapping_configuration_v1")
data class FeedReverseMappingConfiguration(
    @field:PrimaryKey
    val key: FeedReverseMappingConfigurationPrimaryKey,
) {

    companion object {
        fun of(
            feedMappingConfiguration: FeedMappingConfiguration,
        ) = FeedReverseMappingConfiguration(
            key = FeedReverseMappingConfigurationPrimaryKey(
                workspaceId = feedMappingConfiguration.key.workspaceId,
                feedComponentId = feedMappingConfiguration.key.feedComponentId,
                sourceResourceId = feedMappingConfiguration.key.sourceResourceId,
                sourceComponentId = feedMappingConfiguration.key.sourceComponentId,
                eventAction = feedMappingConfiguration.key.eventAction,
                targetResourceId = feedMappingConfiguration.key.targetResourceId,
                targetComponentId = feedMappingConfiguration.key.targetComponentId,
            ),
        )
    }

}

@PrimaryKeyClass
data class FeedReverseMappingConfigurationPrimaryKey(
    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 1)
    val workspaceId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 2)
    val sourceResourceId: ResourceId,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 3)
    val sourceComponentId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 4)
    val eventAction: EventAction,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 5)
    val targetResourceId: ResourceId,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 6)
    val targetComponentId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 7)
    val feedComponentId: String,
)
