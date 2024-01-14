package com.story.core.domain.reaction

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ReactionReverseRepository : CoroutineCrudRepository<ReactionReverse, ReactionReversePrimaryKey> {

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
