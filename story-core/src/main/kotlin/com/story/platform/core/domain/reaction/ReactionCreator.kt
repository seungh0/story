package com.story.platform.core.domain.reaction

import com.story.platform.core.infrastructure.cassandra.executeCoroutine
import com.story.platform.core.infrastructure.cassandra.upsert
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.stereotype.Service

@Service
class ReactionCreator(
    private val reactionRepository: ReactionRepository,
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
    private val reactionCountRepository: ReactionCountRepository,
) {

    suspend fun upsert(
        workspaceId: String,
        componentId: String,
        targetId: String,
        accountId: String,
        optionIds: Set<String>,
    ): ReactionCommandResponse {
        val oldReaction = reactionRepository.findById(
            ReactionPrimaryKey.of(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                accountId = accountId,
            )
        )
        if (oldReaction == null) {
            val newReaction = Reaction.of(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                accountId = accountId,
                optionIds = optionIds,
            )
            reactiveCassandraOperations.batchOps()
                .upsert(newReaction)
                .upsert(ReactionReverse.from(newReaction))
                .executeCoroutine()

            reactionCountRepository.increaseBulk(
                keys = optionIds.map { optionId ->
                    ReactionCountKey(
                        workspaceId = workspaceId,
                        componentId = componentId,
                        targetId = targetId,
                        optionId = optionId,
                    )
                }.toSet()
            )

            return ReactionCommandResponse.created(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                accountId = accountId,
                createdOptionIds = optionIds,
            )
        }

        val deletedOptionIds = oldReaction.optionIds - optionIds
        val createdOptionIds = optionIds - oldReaction.optionIds

        val newReaction = oldReaction.cloneWithOptionIds(optionIds = optionIds)
        reactiveCassandraOperations.batchOps()
            .upsert(newReaction)
            .upsert(ReactionReverse.from(newReaction))
            .executeCoroutine()

        reactionCountRepository.increaseBulk(
            keys = createdOptionIds.map { optionId ->
                ReactionCountKey(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    targetId = targetId,
                    optionId = optionId,
                )
            }.toSet()
        )

        reactionCountRepository.decreaseBulk(
            keys = deletedOptionIds.map { optionId ->
                ReactionCountKey(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    targetId = targetId,
                    optionId = optionId,
                )
            }.toSet()
        )

        return ReactionCommandResponse.updated(
            workspaceId = workspaceId,
            componentId = componentId,
            targetId = targetId,
            accountId = accountId,
            createdOptionIds = createdOptionIds,
            deletedOptionIds = deletedOptionIds,
        )
    }

}
