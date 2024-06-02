package com.story.api.application.reaction

import com.story.api.application.component.ComponentCheckHandler
import com.story.core.common.annotation.HandlerAdapter
import com.story.core.common.utils.mapToSet
import com.story.core.domain.emotion.Emotion
import com.story.core.domain.emotion.EmotionReader
import com.story.core.domain.reaction.ReactionReader
import com.story.core.domain.resource.ResourceId

@HandlerAdapter
class ReactionRetrieveHandler(
    private val reactionReader: ReactionReader,
    private val componentCheckHandler: ComponentCheckHandler,
    private val emotionReader: EmotionReader,
) {

    suspend fun getReaction(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        request: ReactionGetRequest,
        requestUserId: String?,
    ): ReactionResponse {
        componentCheckHandler.checkExistsComponent(
            workspaceId = workspaceId,
            resourceId = ResourceId.REACTIONS,
            componentId = componentId,
        )

        val reaction = reactionReader.getReaction(
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

        return ReactionResponse.of(reaction = reaction, emotions = emotions)
    }

    private suspend fun getEmotions(
        includeUnselectedEmotions: Boolean,
        workspaceId: String,
        componentId: String,
        emotionIds: Set<String>,
    ): Map<String, Emotion> {
        if (includeUnselectedEmotions) {
            return emotionReader.listEmotions(
                workspaceId = workspaceId,
                resourceId = ResourceId.REACTIONS,
                componentId = componentId,
            ).data.associateBy { emotion -> emotion.emotionId }
        }
        return emotionReader.getEmotions(
            workspaceId = workspaceId,
            resourceId = ResourceId.REACTIONS,
            componentId = componentId,
            emotionIds = emotionIds,
        )
    }

    suspend fun listReactions(
        workspaceId: String,
        componentId: String,
        request: ReactionListRequest,
        requestUserId: String?,
    ): ReactionListResponse {
        componentCheckHandler.checkExistsComponent(
            workspaceId = workspaceId,
            resourceId = ResourceId.REACTIONS,
            componentId = componentId,
        )

        val reactions = reactionReader.listReactions(
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

        return ReactionListResponse.of(reactions = reactions, emotions = emotions)
    }

}
