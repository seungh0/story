package com.story.platform.api.domain.emotion

import com.story.platform.api.domain.component.ComponentCheckHandler
import com.story.platform.core.common.annotation.HandlerAdapter
import com.story.platform.core.domain.emotion.EmotionModifier
import com.story.platform.core.domain.resource.ResourceId

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
