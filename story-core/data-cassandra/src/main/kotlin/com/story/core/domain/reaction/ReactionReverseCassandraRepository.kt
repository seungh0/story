package com.story.core.domain.reaction

import com.story.core.infrastructure.cassandra.CassandraBasicRepository

interface ReactionReverseCassandraRepository : CassandraBasicRepository<ReactionReverseEntity, ReactionReversePrimaryKey> {

    suspend fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyUserIdAndKeyDistributionKeyAndKeySpaceIdIn(
        workspaceId: String,
        componentId: String,
        userId: String,
        distributionKey: String,
        spaceIds: Collection<String>,
    ): List<ReactionReverseEntity>

    suspend fun findByKeyWorkspaceIdAndKeyComponentIdAndKeyUserIdAndKeyDistributionKeyAndKeySpaceId(
        workspaceId: String,
        componentId: String,
        userId: String,
        distributionKey: String,
        spaceId: String,
    ): ReactionReverseEntity?

}
