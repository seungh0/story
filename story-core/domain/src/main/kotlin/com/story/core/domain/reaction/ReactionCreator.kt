package com.story.core.domain.reaction

import org.springframework.stereotype.Service

@Service
class ReactionCreator(
    private val reactionRepository: ReactionRepository,
) {

    suspend fun upsertReaction(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        userId: String,
        emotionIds: Set<String>,
    ): ReactionWriteResponse {
        val reaction = reactionRepository.findById(
            workspaceId = workspaceId,
            componentId = componentId,
            spaceId = spaceId,
            userId = userId,
        )
        if (reaction == null) {
            reactionRepository.create(
                workspaceId = workspaceId,
                componentId = componentId,
                spaceId = spaceId,
                userId = userId,
                emotionIds = emotionIds,
            )

            return ReactionWriteResponse.created(
                workspaceId = workspaceId,
                componentId = componentId,
                spaceId = spaceId,
                actorId = userId,
                createdOptionIds = emotionIds,
            )
        }

        val deletedOptionIds = reaction.emotionIds - emotionIds
        val createdOptionIds = emotionIds - reaction.emotionIds

        reactionRepository.create(
            workspaceId = workspaceId,
            componentId = componentId,
            spaceId = spaceId,
            userId = userId,
            emotionIds = emotionIds,
        )

        return ReactionWriteResponse.updated(
            workspaceId = workspaceId,
            componentId = componentId,
            spaceId = spaceId,
            actorId = userId,
            createdOptionIds = createdOptionIds,
            deletedOptionIds = deletedOptionIds,
        )
    }

}
