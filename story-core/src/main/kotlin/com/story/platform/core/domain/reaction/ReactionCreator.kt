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
        spaceId: String,
        accountId: String,
        emotionIds: Set<String>,
    ): ReactionCreateResponse {
        val oldReaction = reactionRepository.findById(
            ReactionPrimaryKey.of(
                workspaceId = workspaceId,
                componentId = componentId,
                spaceId = spaceId,
                accountId = accountId,
            )
        )
        if (oldReaction == null) {
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

            val emotionCountMap = reactionCountRepository.increaseBulk(
                keys = emotionIds.map { optionId ->
                    ReactionCountKey(
                        workspaceId = workspaceId,
                        componentId = componentId,
                        spaceId = spaceId,
                        emotionId = optionId,
                    )
                }.toSet()
            )

            return ReactionCreateResponse.created(
                workspaceId = workspaceId,
                componentId = componentId,
                spaceId = spaceId,
                accountId = accountId,
                createdOptionIds = emotionIds,
                totalEmotionsCount = emotionCountMap.values.sum(),
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

        val emotionCountMap = reactionCountRepository.increaseBulk(
            keys = createdOptionIds.map { optionId ->
                ReactionCountKey(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    spaceId = spaceId,
                    emotionId = optionId,
                )
            }.toSet()
        )

        reactionCountRepository.decreaseBulk(
            keys = deletedOptionIds.map { optionId ->
                ReactionCountKey(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    spaceId = spaceId,
                    emotionId = optionId,
                )
            }.toSet()
        )

        return ReactionCreateResponse.updated(
            workspaceId = workspaceId,
            componentId = componentId,
            spaceId = spaceId,
            accountId = accountId,
            createdOptionIds = createdOptionIds,
            deletedOptionIds = deletedOptionIds,
            totalEmotionsCount = emotionCountMap.values.sum(),
        )
    }

}
