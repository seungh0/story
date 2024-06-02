package com.story.core.domain.emotion

import com.story.core.domain.resource.ResourceId
import org.springframework.stereotype.Service

@Service
class EmotionRemover(
    private val emotionRepository: EmotionWriteRepository,
) {

    suspend fun removeEmotion(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        emotionId: String,
    ) {
        emotionRepository.delete(
            workspaceId = workspaceId,
            resourceId = resourceId,
            componentId = componentId,
            emotionId = emotionId,
        )
    }

}
