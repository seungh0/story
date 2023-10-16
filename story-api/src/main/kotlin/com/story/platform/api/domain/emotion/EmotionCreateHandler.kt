package com.story.platform.api.domain.emotion

import com.story.platform.api.domain.component.ComponentCheckHandler
import com.story.platform.core.common.annotation.HandlerAdapter
import com.story.platform.core.domain.emotion.EmotionCreator
import com.story.platform.core.domain.resource.ResourceId

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
