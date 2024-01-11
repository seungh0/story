package com.story.core.domain.emotion

import com.story.core.domain.resource.ResourceId
import org.springframework.stereotype.Service

@Service
class EmotionRemover(
    private val emotionRepository: EmotionRepository,
) {

    suspend fun removeEmotion(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        emotionId: String,
    ) {
        emotionRepository.deleteById(
            EmotionPrimaryKey(
                workspaceId = workspaceId,
                resourceId = resourceId,
                componentId = componentId,
                emotionId = emotionId,
            )
        )
    }

}
