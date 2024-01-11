package com.story.api.application.emotion

import com.story.api.application.component.ComponentCheckHandler
import com.story.core.common.annotation.HandlerAdapter
import com.story.core.domain.emotion.EmotionCreator
import com.story.core.domain.resource.ResourceId

@HandlerAdapter
class EmotionCreateHandler(
    private val componentCheckHandler: ComponentCheckHandler,
    private val emotionCreator: EmotionCreator,
) {

    suspend fun createEmotion(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        emotionId: String,
        request: EmotionCreateApiRequest,
    ) {
        componentCheckHandler.checkExistsComponent(
            workspaceId = workspaceId,
            resourceId = resourceId,
            componentId = componentId,
        )

        emotionCreator.createEmotion(
            workspaceId = workspaceId,
            resourceId = resourceId,
            componentId = componentId,
            emotionId = emotionId,
            priority = request.priority,
            image = request.image,
        )
    }

}
