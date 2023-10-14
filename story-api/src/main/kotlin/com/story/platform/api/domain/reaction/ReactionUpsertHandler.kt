package com.story.platform.api.domain.reaction

import com.story.platform.api.domain.component.ComponentCheckHandler
import com.story.platform.core.common.annotation.HandlerAdapter
import com.story.platform.core.common.utils.mapToSet
import com.story.platform.core.domain.emotion.EmotionRetriever
import com.story.platform.core.domain.reaction.ReactionCreator
import com.story.platform.core.domain.reaction.ReactionEventProducer
import com.story.platform.core.domain.resource.ResourceId

@HandlerAdapter
class ReactionUpsertHandler(
    private val reactionCreator: ReactionCreator,
    private val componentCheckHandler: ComponentCheckHandler,
    private val reactionEventProducer: ReactionEventProducer,
    private val emotionRetriever: EmotionRetriever,
) {

    suspend fun upsertReaction(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        accountId: String,
        request: ReactionUpsertApiRequest,
    ) {
        componentCheckHandler.checkExistsComponent(
            workspaceId = workspaceId,
            resourceId = ResourceId.REACTION,
            componentId = componentId,
        )

        emotionRetriever.validateExistsEmotions(
            workspaceId = workspaceId,
            componentId = componentId,
            spaceId = spaceId,
            emotionIds = request.emotions.mapToSet { emotion -> emotion.emotionId }
        )

        val change = reactionCreator.upsertReaction(
            workspaceId = workspaceId,
            componentId = componentId,
            spaceId = spaceId,
            accountId = accountId,
            emotionIds = request.emotions.mapToSet { option -> option.emotionId },
        )

        reactionEventProducer.publishEvent(change = change)
    }

}
