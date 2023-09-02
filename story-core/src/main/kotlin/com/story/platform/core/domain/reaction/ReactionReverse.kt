package com.story.platform.core.domain.reaction

import com.story.platform.core.common.distribution.XLargeDistributionKey
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("reaction_reverse_v1")
data class ReactionReverse(
    @field:PrimaryKey
    val key: ReactionReversePrimaryKey,
    val optionIds: Set<String>,
) {

    companion object {
        fun of(
            workspaceId: String,
            componentId: String,
            targetId: String,
            accountId: String,
            optionIds: Set<String>,
        ) = ReactionReverse(
            key = ReactionReversePrimaryKey.of(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                accountId = accountId,
            ),
            optionIds = optionIds,
        )

        fun from(
            reaction: Reaction,
        ) = ReactionReverse(
            key = ReactionReversePrimaryKey.of(
                workspaceId = reaction.key.workspaceId,
                componentId = reaction.key.componentId,
                accountId = reaction.key.accountId,
                targetId = reaction.key.targetId,
            ),
            optionIds = reaction.optionIds,
        )
    }

}

@PrimaryKeyClass
data class ReactionReversePrimaryKey(
    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 1)
    val workspaceId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 2)
    val componentId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 3)
    val accountId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 4)
    val distributionKey: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 5)
    val targetId: String,
) {

    companion object {
        fun of(
            workspaceId: String,
            componentId: String,
            targetId: String,
            accountId: String,
        ) = ReactionReversePrimaryKey(
            workspaceId = workspaceId,
            componentId = componentId,
            accountId = accountId,
            distributionKey = XLargeDistributionKey.makeKey(targetId).key,
            targetId = targetId,
        )
    }

}
