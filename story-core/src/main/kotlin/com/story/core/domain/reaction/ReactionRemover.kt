package com.story.core.domain.reaction

import com.story.core.infrastructure.cassandra.executeCoroutine
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.stereotype.Service

@Service
class ReactionRemover(
    private val reactionRepository: ReactionRepository,
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
) {

    suspend fun removeReaction(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        userId: String,
    ): ReactionChangeResponse {
        val reaction = reactionRepository.findById(
            ReactionPrimaryKey.of(
                workspaceId = workspaceId,
                componentId = componentId,
                spaceId = spaceId,
                userId = userId,
            )
        ) ?: return ReactionChangeResponse.deleted(
            workspaceId = workspaceId,
            componentId = componentId,
            spaceId = spaceId,
            actorId = userId,
            deletedOptionIds = emptySet(),
        )

        reactiveCassandraOperations.batchOps()
            .delete(reaction)
            .delete(ReactionReverse.from(reaction))
            .executeCoroutine()

        return ReactionChangeResponse.deleted(
            workspaceId = workspaceId,
            componentId = componentId,
            spaceId = spaceId,
            actorId = userId,
            deletedOptionIds = reaction.emotionIds,
        )
    }

}
