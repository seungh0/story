package com.story.api.application.reaction

import com.story.api.application.component.ComponentCheckHandler
import com.story.core.common.annotation.HandlerAdapter
import com.story.core.common.utils.mapToSet
import com.story.core.domain.emotion.Emotion
import com.story.core.domain.emotion.EmotionRetriever
import com.story.core.domain.reaction.ReactionRetriever
import com.story.core.domain.resource.ResourceId

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
        request: ReactionGetApiRequest,
        requestUserId: String?,
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
            requestUserId = requestUserId,
        )

        val emotions = getEmotions(
            includeUnselectedEmotions = request.includeUnselectedEmotions,
            workspaceId = workspaceId,
            componentId = componentId,
            emotionIds = reaction.emotions.mapToSet { emotion -> emotion.emotionId },
        )

        return ReactionApiResponse.of(reaction = reaction, emotions = emotions)
    }

    private suspend fun getEmotions(
        includeUnselectedEmotions: Boolean,
        workspaceId: String,
        componentId: String,
        emotionIds: Set<String>,
    ): Map<String, Emotion> {
        if (includeUnselectedEmotions) {
            return emotionRetriever.listEmotions(
                workspaceId = workspaceId,
                resourceId = ResourceId.REACTIONS,
                componentId = componentId,
            ).data.associateBy { emotion -> emotion.emotionId }
        }
        return emotionRetriever.getEmotions(
            workspaceId = workspaceId,
            resourceId = ResourceId.REACTIONS,
            componentId = componentId,
            emotionIds = emotionIds,
        )
    }

    suspend fun listReactions(
        workspaceId: String,
        componentId: String,
        request: ReactionListApiRequest,
        requestUserId: String?,
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
            requestUserId = requestUserId,
        )

        val emotionIds = reactions.asSequence()
            .flatMap { reaction -> reaction.emotions.map { emotion -> emotion.emotionId } }
            .toSet()

        val emotions = getEmotions(
            includeUnselectedEmotions = request.includeUnselectedEmotions,
            workspaceId = workspaceId,
            componentId = componentId,
            emotionIds = emotionIds,
        )

        return ReactionListApiResponse.of(reactions = reactions, emotions = emotions)
    }

}
