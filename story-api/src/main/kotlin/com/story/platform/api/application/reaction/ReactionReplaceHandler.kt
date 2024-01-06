package com.story.platform.api.application.reaction

import com.story.platform.api.application.component.ComponentCheckHandler
import com.story.platform.core.common.annotation.HandlerAdapter
import com.story.platform.core.common.utils.mapToSet
import com.story.platform.core.domain.emotion.EmotionRetriever
import com.story.platform.core.domain.reaction.ReactionChangeResponse
import com.story.platform.core.domain.reaction.ReactionCreator
import com.story.platform.core.domain.reaction.ReactionEventProducer
import com.story.platform.core.domain.reaction.ReactionRemover
import com.story.platform.core.domain.resource.ResourceId

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
        accountId: String,
        request: ReactionReplaceApiRequest,
    ) {
        componentCheckHandler.checkExistsComponent(
            workspaceId = workspaceId,
            resourceId = ResourceId.REACTIONS,
            componentId = componentId,
        )
        val change = replaceReactions(request, workspaceId, componentId, spaceId, accountId)
        reactionEventProducer.publishEvent(change = change)
    }

    private suspend fun replaceReactions(
        request: ReactionReplaceApiRequest,
        workspaceId: String,
        componentId: String,
        spaceId: String,
        accountId: String,
    ): ReactionChangeResponse {
        if (request.isClearRequest()) {
            return reactionRemover.removeReaction(
                workspaceId = workspaceId,
                componentId = componentId,
                spaceId = spaceId,
                accountId = accountId,
            )
        }
        return upsertReactions(workspaceId, componentId, request, spaceId, accountId)
    }

    private suspend fun upsertReactions(
        workspaceId: String,
        componentId: String,
        request: ReactionReplaceApiRequest,
        spaceId: String,
        accountId: String,
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
            accountId = accountId,
            emotionIds = request.emotions.mapToSet { option -> option.emotionId },
        )
    }

}
