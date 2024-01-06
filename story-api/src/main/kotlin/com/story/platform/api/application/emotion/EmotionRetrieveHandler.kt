package com.story.platform.api.application.emotion

import com.story.platform.api.application.component.ComponentCheckHandler
import com.story.platform.core.common.annotation.HandlerAdapter
import com.story.platform.core.domain.emotion.EmotionRetriever
import com.story.platform.core.domain.resource.ResourceId

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
