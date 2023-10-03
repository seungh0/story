package com.story.platform.api.domain.reaction

import com.story.platform.api.domain.component.ComponentCheckHandler
import com.story.platform.core.common.spring.HandlerAdapter
import com.story.platform.core.domain.reaction.ReactionEventProducer
import com.story.platform.core.domain.reaction.ReactionRemover
import com.story.platform.core.domain.resource.ResourceId

@HandlerAdapter
class ReactionRemoveHandler(
    private val componentCheckHandler: ComponentCheckHandler,
    private val reactionRemover: ReactionRemover,
    private val reactionEventProducer: ReactionEventProducer,
) {

    suspend fun removeReaction(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        accountId: String,
    ) {
        componentCheckHandler.checkExistsComponent(
            workspaceId = workspaceId,
            resourceId = ResourceId.REACTION,
            componentId = componentId,
        )

        val change = reactionRemover.removeReaction(
            workspaceId = workspaceId,
            componentId = componentId,
            spaceId = spaceId,
            accountId = accountId,
        )

        reactionEventProducer.publishEvent(change = change)
    }

}
