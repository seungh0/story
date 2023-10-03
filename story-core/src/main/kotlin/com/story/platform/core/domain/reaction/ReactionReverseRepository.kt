package com.story.platform.core.domain.reaction

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ReactionReverseRepository : CoroutineCrudRepository<ReactionReverse, ReactionReversePrimaryKey> {

    suspend fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyAccountIdAndKeyDistributionKeyAndKeySpaceIdIn(
        workspaceId: String,
        componentId: String,
        accountId: String,
        distributionKey: String,
        spaceIds: Collection<String>,
    ): List<ReactionReverse>

    suspend fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyAccountIdAndKeyDistributionKeyAndKeySpaceId(
        workspaceId: String,
        componentId: String,
        accountId: String,
        distributionKey: String,
        spaceId: String,
    ): ReactionReverse

}
