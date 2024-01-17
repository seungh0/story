package com.story.core.domain.reaction

import com.story.core.infrastructure.cassandra.CassandraEntity
import com.story.core.infrastructure.cassandra.CassandraKey
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("reaction_v1")
data class Reaction(
    @field:PrimaryKey
    override val key: ReactionPrimaryKey,
    var emotionIds: Set<String>,
) : CassandraEntity {

    companion object {
        fun of(
            workspaceId: String,
            componentId: String,
            spaceId: String,
            userId: String,
            emotionIds: Set<String>,
        ) = Reaction(
            key = ReactionPrimaryKey.of(
                workspaceId = workspaceId,
                componentId = componentId,
                spaceId = spaceId,
                userId = userId,
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
    val spaceId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 4)
    val distributionKey: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 5)
    val userId: String,
) : CassandraKey {

    companion object {
        fun of(
            workspaceId: String,
            componentId: String,
            spaceId: String,
            userId: String,
        ) = ReactionPrimaryKey(
            workspaceId = workspaceId,
            componentId = componentId,
            spaceId = spaceId,
            distributionKey = ReactionDistributionKey.makeKey(userId),
            userId = userId,
        )
    }

}
