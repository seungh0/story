package com.story.core.domain.reaction

import com.story.core.infrastructure.cassandra.CassandraBasicRepository

interface ReactionReverseRepository : CassandraBasicRepository<ReactionReverse, ReactionReversePrimaryKey> {

    suspend fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyUserIdAndKeyDistributionKeyAndKeySpaceIdIn(
        workspaceId: String,
        componentId: String,
        userId: String,
        distributionKey: String,
        spaceIds: Collection<String>,
    ): List<ReactionReverse>

    suspend fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyUserIdAndKeyDistributionKeyAndKeySpaceId(
        workspaceId: String,
        componentId: String,
        userId: String,
        distributionKey: String,
        spaceId: String,
    ): ReactionReverse

}
