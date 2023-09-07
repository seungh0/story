package com.story.platform.core.domain.reaction

import com.story.platform.core.common.distribution.XLargeDistributionKey
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("reaction_v1")
data class Reaction(
    @field:PrimaryKey
    val key: ReactionPrimaryKey,
    var emotionIds: Set<String>,
) {

    companion object {
        fun of(
            workspaceId: String,
            componentId: String,
            targetId: String,
            accountId: String,
            emotionIds: Set<String>,
        ) = Reaction(
            key = ReactionPrimaryKey.of(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                accountId = accountId,
            ),
            emotionIds = emotionIds,
        )
    }

}

@PrimaryKeyClass
data class ReactionPrimaryKey(
    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 1)
    val workspaceId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 2)
    val componentId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 3)
    val targetId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 4)
    val distributionKey: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 5)
    val accountId: String,
) {

    companion object {
        fun of(
            workspaceId: String,
            componentId: String,
            targetId: String,
            accountId: String,
        ) = ReactionPrimaryKey(
            workspaceId = workspaceId,
            componentId = componentId,
            targetId = targetId,
            distributionKey = XLargeDistributionKey.makeKey(accountId).key,
            accountId = accountId,
        )
    }

}
