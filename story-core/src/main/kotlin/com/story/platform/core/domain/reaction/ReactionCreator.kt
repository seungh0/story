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
        emotionIds: Set<String>,
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
                emotionIds = emotionIds,
            )
            reactiveCassandraOperations.batchOps()
                .upsert(newReaction)
                .upsert(ReactionReverse.from(newReaction))
                .executeCoroutine()

            reactionCountRepository.increaseBulk(
                keys = emotionIds.map { optionId ->
                    ReactionCountKey(
                        workspaceId = workspaceId,
                        componentId = componentId,
                        targetId = targetId,
                        emotionId = optionId,
                    )
                }.toSet()
            )

            return ReactionCommandResponse.created(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                accountId = accountId,
                createdOptionIds = emotionIds,
            )
        }

        val deletedOptionIds = oldReaction.emotionIds - emotionIds
        val createdOptionIds = emotionIds - oldReaction.emotionIds

        val newReaction = oldReaction.copy(
            emotionIds = emotionIds,
        )
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
                    emotionId = optionId,
                )
            }.toSet()
        )

        reactionCountRepository.decreaseBulk(
            keys = deletedOptionIds.map { optionId ->
                ReactionCountKey(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    targetId = targetId,
                    emotionId = optionId,
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
