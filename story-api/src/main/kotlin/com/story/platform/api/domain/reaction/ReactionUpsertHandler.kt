package com.story.platform.api.domain.reaction

import com.story.platform.api.domain.component.ComponentCheckHandler
import com.story.platform.core.common.spring.HandlerAdapter
import com.story.platform.core.domain.reaction.ReactionCreator
import com.story.platform.core.domain.resource.ResourceId

@HandlerAdapter
class ReactionUpsertHandler(
    private val reactionCreator: ReactionCreator,
    private val componentCheckHandler: ComponentCheckHandler,
) {

    suspend fun upsert(
        workspaceId: String,
        componentId: String,
        targetId: String,
        request: ReactionUpsertApiRequest,
    ) {
        componentCheckHandler.checkExistsComponent(
            workspaceId = workspaceId,
            resourceId = ResourceId.REACTION,
            componentId = componentId,
        )

        reactionCreator.upsert(
            workspaceId = workspaceId,
            componentId = componentId,
            targetId = targetId,
            accountId = request.accountId,
            emotionIds = request.emotions.map { option -> option.emotionId }.toSet(),
        )
    }

}
