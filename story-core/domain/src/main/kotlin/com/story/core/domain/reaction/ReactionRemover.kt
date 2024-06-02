package com.story.core.domain.reaction

import org.springframework.stereotype.Service

@Service
class ReactionRemover(
    private val reactionRepository: ReactionRepository,
) {

    suspend fun removeReaction(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        userId: String,
    ): ReactionWriteResponse {
        val deletedEmotionIds = reactionRepository.delete(
            workspaceId = workspaceId,
            componentId = componentId,
            spaceId = spaceId,
            userId = userId,
        )
        if (deletedEmotionIds.isEmpty()) {
            return ReactionWriteResponse.deleted(
                workspaceId = workspaceId,
                componentId = componentId,
                spaceId = spaceId,
                actorId = userId,
                deletedOptionIds = emptySet(),
            )
        }

        return ReactionWriteResponse.deleted(
            workspaceId = workspaceId,
            componentId = componentId,
            spaceId = spaceId,
            actorId = userId,
            deletedOptionIds = deletedEmotionIds,
        )
    }

}
