package com.story.api.application.reaction

import com.story.api.application.component.ComponentCheckHandler
import com.story.core.common.annotation.HandlerAdapter
import com.story.core.common.utils.mapToSet
import com.story.core.domain.emotion.EmotionRetriever
import com.story.core.domain.reaction.ReactionChangeResponse
import com.story.core.domain.reaction.ReactionCreator
import com.story.core.domain.reaction.ReactionEventProducer
import com.story.core.domain.reaction.ReactionRemover
import com.story.core.domain.resource.ResourceId

@HandlerAdapter
class ReactionReplaceHandler(
    private val reactionCreator: ReactionCreator,
    private val componentCheckHandler: ComponentCheckHandler,
    private val reactionEventProducer: ReactionEventProducer,
    private val emotionRetriever: EmotionRetriever,
    private val reactionRemover: ReactionRemover,
) {

    suspend fun replaceReactions(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        userId: String,
        request: ReactionReplaceApiRequest,
    ) {
        componentCheckHandler.checkExistsComponent(
            workspaceId = workspaceId,
            resourceId = ResourceId.REACTIONS,
            componentId = componentId,
        )
        val change = replaceReactions(request, workspaceId, componentId, spaceId, userId)
        reactionEventProducer.publishEvent(change = change)
    }

    private suspend fun replaceReactions(
        request: ReactionReplaceApiRequest,
        workspaceId: String,
        componentId: String,
        spaceId: String,
        userId: String,
    ): ReactionChangeResponse {
        if (request.isClearRequest()) {
            return reactionRemover.removeReaction(
                workspaceId = workspaceId,
                componentId = componentId,
                spaceId = spaceId,
                userId = userId,
            )
        }
        return upsertReactions(workspaceId, componentId, request, spaceId, userId)
    }

    private suspend fun upsertReactions(
        workspaceId: String,
        componentId: String,
        request: ReactionReplaceApiRequest,
        spaceId: String,
        userId: String,
    ): ReactionChangeResponse {
        emotionRetriever.validateExistsEmotions(
            workspaceId = workspaceId,
            resourceId = ResourceId.REACTIONS,
            componentId = componentId,
            emotionIds = request.emotions.mapToSet { emotion -> emotion.emotionId }
        )

        return reactionCreator.upsertReaction(
            workspaceId = workspaceId,
            componentId = componentId,
            spaceId = spaceId,
            userId = userId,
            emotionIds = request.emotions.mapToSet { option -> option.emotionId },
        )
    }

}
