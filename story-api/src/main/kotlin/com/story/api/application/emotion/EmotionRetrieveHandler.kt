package com.story.api.application.emotion

import com.story.api.application.component.ComponentCheckHandler
import com.story.core.common.annotation.HandlerAdapter
import com.story.core.domain.emotion.EmotionReader
import com.story.core.domain.resource.ResourceId

@HandlerAdapter
class EmotionRetrieveHandler(
    private val componentCheckHandler: ComponentCheckHandler,
    private val emotionReader: EmotionReader,
) {

    suspend fun listEmotions(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
    ): EmotionListResponse {
        componentCheckHandler.checkExistsComponent(
            workspaceId = workspaceId,
            resourceId = resourceId,
            componentId = componentId,
        )

        val emotions = emotionReader.listEmotions(
            workspaceId = workspaceId,
            resourceId = resourceId,
            componentId = componentId,
        )

        return EmotionListResponse.of(emotions = emotions)
    }

}
