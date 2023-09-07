package com.story.platform.core.domain.reaction

import com.story.platform.core.infrastructure.cassandra.executeCoroutine
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.stereotype.Service

@Service
class ReactionRemover(
    private val reactionRepository: ReactionRepository,
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
    private val reactionCountRepository: ReactionCountRepository,
) {

    suspend fun remove(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        accountId: String,
    ): ReactionDeleteResponse {
        val reaction = reactionRepository.findById(
            ReactionPrimaryKey.of(
                workspaceId = workspaceId,
                componentId = componentId,
                spaceId = spaceId,
                accountId = accountId,
            )
        ) ?: return ReactionDeleteResponse.deleted(
            workspaceId = workspaceId,
            componentId = componentId,
            spaceId = spaceId,
            accountId = accountId,
            deletedOptionIds = emptySet(),
        )

        reactiveCassandraOperations.batchOps()
            .delete(reaction)
            .delete(ReactionReverse.from(reaction))
            .executeCoroutine()

        reactionCountRepository.decreaseBulk(
            keys = reaction.emotionIds.map { optionId ->
                ReactionCountKey(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    spaceId = spaceId,
                    emotionId = optionId,
                )
            }.toSet()
        )

        return ReactionDeleteResponse.deleted(
            workspaceId = workspaceId,
            componentId = componentId,
            spaceId = spaceId,
            accountId = accountId,
            deletedOptionIds = reaction.emotionIds,
        )
    }

}
