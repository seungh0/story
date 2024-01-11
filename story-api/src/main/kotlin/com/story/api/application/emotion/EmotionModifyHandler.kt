package com.story.api.application.emotion

import com.story.api.application.component.ComponentCheckHandler
import com.story.core.common.annotation.HandlerAdapter
import com.story.core.domain.emotion.EmotionModifier
import com.story.core.domain.resource.ResourceId

@HandlerAdapter
class EmotionModifyHandler(
    private val componentCheckHandler: ComponentCheckHandler,
    private val emotionModifier: EmotionModifier,
) {

    suspend fun modifyEmotion(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        emotionId: String,
        request: EmotionModifyApiRequest,
    ) {
        componentCheckHandler.checkExistsComponent(
            workspaceId = workspaceId,
            resourceId = resourceId,
            componentId = componentId,
        )

        emotionModifier.modifyEmotion(
            workspaceId = workspaceId,
            resourceId = resourceId,
            componentId = componentId,
            emotionId = emotionId,
            priority = request.priority,
            image = request.image,
        )
    }

}
