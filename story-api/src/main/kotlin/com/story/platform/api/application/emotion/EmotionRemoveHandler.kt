package com.story.platform.api.application.emotion

import com.story.platform.api.application.component.ComponentCheckHandler
import com.story.platform.core.common.annotation.HandlerAdapter
import com.story.platform.core.domain.emotion.EmotionRemover
import com.story.platform.core.domain.resource.ResourceId

@HandlerAdapter
class EmotionRemoveHandler(
    private val componentCheckHandler: ComponentCheckHandler,
    private val emotionRemover: EmotionRemover,
) {

    suspend fun removeEmotion(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        emotionId: String,
    ) {
        componentCheckHandler.checkExistsComponent(
            workspaceId = workspaceId,
            resourceId = resourceId,
            componentId = componentId,
        )

        emotionRemover.removeEmotion(
            workspaceId = workspaceId,
            resourceId = resourceId,
            componentId = componentId,
            emotionId = emotionId,
        )
    }

}
