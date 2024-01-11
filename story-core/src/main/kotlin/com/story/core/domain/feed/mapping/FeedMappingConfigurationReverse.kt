package com.story.core.domain.feed.mapping

import com.story.core.domain.resource.ResourceId
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("feed_mapping_configuration_reverse_v1")
data class FeedMappingConfigurationReverse(
    @field:PrimaryKey
    val key: FeedMappingConfigurationReversePrimaryKey,
) {

    companion object {
        fun of(
            feedMappingConfiguration: FeedMappingConfiguration,
        ) = FeedMappingConfigurationReverse(
            key = FeedMappingConfigurationReversePrimaryKey(
                workspaceId = feedMappingConfiguration.key.workspaceId,
                feedComponentId = feedMappingConfiguration.key.feedComponentId,
                sourceResourceId = feedMappingConfiguration.key.sourceResourceId,
                sourceComponentId = feedMappingConfiguration.key.sourceComponentId,
                subscriptionComponentId = feedMappingConfiguration.key.subscriptionComponentId,
            ),
        )
    }

}

@PrimaryKeyClass
data class FeedMappingConfigurationReversePrimaryKey(
    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 1)
    val workspaceId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 2)
    val sourceResourceId: ResourceId,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 3)
    val sourceComponentId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 4)
    val subscriptionComponentId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 5)
    val feedComponentId: String,
)
