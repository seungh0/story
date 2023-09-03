package com.story.platform.api.domain.reaction

import com.story.platform.api.domain.component.ComponentCheckHandler
import com.story.platform.core.common.spring.HandlerAdapter
import com.story.platform.core.domain.reaction.ReactionRemover
import com.story.platform.core.domain.resource.ResourceId

@HandlerAdapter
class ReactionRemoveHandler(
    private val componentCheckHandler: ComponentCheckHandler,
    private val reactionRemover: ReactionRemover,
) {

    suspend fun remove(
        workspaceId: String,
        componentId: String,
        targetId: String,
        accountId: String,
    ) {
        componentCheckHandler.checkExistsComponent(
            workspaceId = workspaceId,
            resourceId = ResourceId.REACTION,
            componentId = componentId,
        )

        reactionRemover.remove(
            workspaceId = workspaceId,
            componentId = componentId,
            targetId = targetId,
            accountId = accountId,
        )
    }

}
