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
        targetId: String,
        accountId: String,
    ): ReactionCommandResponse {
        val reaction = reactionRepository.findById(
            ReactionPrimaryKey.of(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                accountId = accountId,
            )
        ) ?: return ReactionCommandResponse.deleted(
            workspaceId = workspaceId,
            componentId = componentId,
            targetId = targetId,
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
                    targetId = targetId,
                    emotionId = optionId,
                )
            }.toSet()
        )

        return ReactionCommandResponse.deleted(
            workspaceId = workspaceId,
            componentId = componentId,
            targetId = targetId,
            accountId = accountId,
            deletedOptionIds = reaction.emotionIds,
        )
    }

}
