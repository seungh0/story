package com.story.platform.core.domain.reaction

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ReactionReverseRepository : CoroutineCrudRepository<ReactionReverse, ReactionReversePrimaryKey> {

    suspend fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyAccountIdAndKeyDistributionKeyAndKeyTargetIdIn(
        workspaceId: String,
        componentId: String,
        accountId: String,
        distributionKey: String,
        targetIds: Collection<String>,
    ): List<ReactionReverse>

}
