package com.story.platform.api.domain.reaction

import com.story.platform.api.domain.component.ComponentCheckHandler
import com.story.platform.core.common.spring.HandlerAdapter
import com.story.platform.core.domain.reaction.ReactionCreator
import com.story.platform.core.domain.reaction.ReactionEventProducer
import com.story.platform.core.domain.resource.ResourceId

@HandlerAdapter
class ReactionUpsertHandler(
    private val reactionCreator: ReactionCreator,
    private val componentCheckHandler: ComponentCheckHandler,
    private val reactionEventProducer: ReactionEventProducer,
) {

    suspend fun upsertReaction(
        workspaceId: String,
        componentId: String,
        spaceIds: String,
        request: ReactionUpsertApiRequest,
    ) {
        componentCheckHandler.checkExistsComponent(
            workspaceId = workspaceId,
            resourceId = ResourceId.REACTION,
            componentId = componentId,
        )

        val change = reactionCreator.upsertReaction(
            workspaceId = workspaceId,
            componentId = componentId,
            spaceId = spaceIds,
            accountId = request.accountId,
            emotionIds = request.emotions.map { option -> option.emotionId }.toSet(),
        )

        reactionEventProducer.publishEvent(change = change)
    }

}
