package com.story.core.domain.reaction

import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("reaction_reverse_v1")
data class ReactionReverseEntity(
    @field:PrimaryKey
    val key: ReactionReversePrimaryKey,
    val emotionIds: Set<String>,
) {

    fun toReaction() = Reaction(
        workspaceId = this.key.workspaceId,
        componentId = this.key.componentId,
        spaceId = this.key.spaceId,
        emotionIds = this.emotionIds,
    )

    companion object {
        fun from(
            reaction: ReactionEntity,
        ) = ReactionReverseEntity(
            key = ReactionReversePrimaryKey.of(
                workspaceId = reaction.key.workspaceId,
                componentId = reaction.key.componentId,
                userId = reaction.key.userId,
                spaceId = reaction.key.spaceId,
            ),
            emotionIds = reaction.emotionIds,
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
    val userId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 4)
    val distributionKey: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 5)
    val spaceId: String,
) {

    companion object {
        fun of(
            workspaceId: String,
            componentId: String,
            userId: String,
            spaceId: String,
        ) = ReactionReversePrimaryKey(
            workspaceId = workspaceId,
            componentId = componentId,
            userId = userId,
            distributionKey = ReactionDistributionKey.makeKey(spaceId),
            spaceId = spaceId,
        )
    }

}
