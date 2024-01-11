package com.story.core.domain.reaction

import com.story.core.infrastructure.cassandra.executeCoroutine
import com.story.core.infrastructure.cassandra.upsert
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.stereotype.Service

@Service
class ReactionCreator(
    private val reactionRepository: ReactionRepository,
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
) {

    suspend fun upsertReaction(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        accountId: String,
        emotionIds: Set<String>,
    ): ReactionChangeResponse {
        val reaction = reactionRepository.findById(
            ReactionPrimaryKey.of(
                workspaceId = workspaceId,
                componentId = componentId,
                spaceId = spaceId,
                accountId = accountId,
            )
        )
        if (reaction == null) {
            val newReaction = Reaction.of(
                workspaceId = workspaceId,
                componentId = componentId,
                spaceId = spaceId,
                accountId = accountId,
                emotionIds = emotionIds,
            )
            reactiveCassandraOperations.batchOps()
                .upsert(newReaction)
                .upsert(ReactionReverse.from(newReaction))
                .executeCoroutine()

            return ReactionChangeResponse.created(
                workspaceId = workspaceId,
                componentId = componentId,
                spaceId = spaceId,
                accountId = accountId,
                createdOptionIds = emotionIds,
            )
        }

        val deletedOptionIds = reaction.emotionIds - emotionIds
        val createdOptionIds = emotionIds - reaction.emotionIds

        val newReaction = reaction.copy(
            emotionIds = emotionIds,
        )
        reactiveCassandraOperations.batchOps()
            .upsert(newReaction)
            .upsert(ReactionReverse.from(newReaction))
            .executeCoroutine()

        return ReactionChangeResponse.updated(
            workspaceId = workspaceId,
            componentId = componentId,
            spaceId = spaceId,
            accountId = accountId,
            createdOptionIds = createdOptionIds,
            deletedOptionIds = deletedOptionIds,
        )
    }

}
