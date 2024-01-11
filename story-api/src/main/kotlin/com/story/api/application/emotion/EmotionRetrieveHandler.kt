package com.story.api.application.emotion

import com.story.api.application.component.ComponentCheckHandler
import com.story.core.common.annotation.HandlerAdapter
import com.story.core.domain.emotion.EmotionRetriever
import com.story.core.domain.resource.ResourceId

@HandlerAdapter
class EmotionRetrieveHandler(
    private val componentCheckHandler: ComponentCheckHandler,
    private val emotionRetriever: EmotionRetriever,
) {

    suspend fun listEmotions(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
    ): EmotionListApiResponse {
        componentCheckHandler.checkExistsComponent(
            workspaceId = workspaceId,
            resourceId = resourceId,
            componentId = componentId,
        )

        val emotions = emotionRetriever.listEmotions(
            workspaceId = workspaceId,
            resourceId = resourceId,
            componentId = componentId,
        )

        return EmotionListApiResponse.of(emotions = emotions)
    }

}
