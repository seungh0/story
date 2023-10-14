package com.story.platform.api.domain.reaction

import com.story.platform.api.domain.component.ComponentCheckHandler
import com.story.platform.core.common.annotation.HandlerAdapter
import com.story.platform.core.common.utils.mapToSet
import com.story.platform.core.domain.emotion.EmotionRetriever
import com.story.platform.core.domain.reaction.ReactionRetriever
import com.story.platform.core.domain.resource.ResourceId

@HandlerAdapter
class ReactionRetrieveHandler(
    private val reactionRetriever: ReactionRetriever,
    private val componentCheckHandler: ComponentCheckHandler,
    private val emotionRetriever: EmotionRetriever,
) {

    suspend fun getReaction(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        requestAccountId: String?,
    ): ReactionApiResponse {
        componentCheckHandler.checkExistsComponent(
            workspaceId = workspaceId,
            resourceId = ResourceId.REACTIONS,
            componentId = componentId,
        )

        val reaction = reactionRetriever.getReaction(
            workspaceId = workspaceId,
            componentId = componentId,
            spaceId = spaceId,
            requestAccountId = requestAccountId,
        )

        val emotions = emotionRetriever.getEmotions(
            workspaceId = workspaceId,
            resourceId = ResourceId.REACTIONS,
            componentId = componentId,
            emotionIds = reaction.emotions.mapToSet { emotion -> emotion.emotionId }
        )

        return ReactionApiResponse.of(reaction = reaction, emotions = emotions)
    }

    suspend fun listReactions(
        workspaceId: String,
        componentId: String,
        request: ReactionListApiRequest,
        requestAccountId: String?,
    ): ReactionListApiResponse {
        componentCheckHandler.checkExistsComponent(
            workspaceId = workspaceId,
            resourceId = ResourceId.REACTIONS,
            componentId = componentId,
        )

        val reactions = reactionRetriever.listReactions(
            workspaceId = workspaceId,
            componentId = componentId,
            spaceIds = request.spaceIds,
            requestAccountId = requestAccountId,
        )

        val emotionIds = reactions.asSequence()
            .flatMap { reaction -> reaction.emotions.map { emotion -> emotion.emotionId } }
            .toSet()

        val emotions = emotionRetriever.getEmotions(
            workspaceId = workspaceId,
            resourceId = ResourceId.REACTIONS,
            componentId = componentId,
            emotionIds = emotionIds,
        )

        return ReactionListApiResponse.of(reactions = reactions, emotions = emotions)
    }

}
