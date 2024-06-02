package com.story.core.domain.emotion

import com.story.core.domain.resource.ResourceId
import org.springframework.stereotype.Service

@Service
class EmotionModifier(
    private val emotionRepository: EmotionWriteRepository,
) {

    suspend fun modifyEmotion(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        emotionId: String,
        priority: Long?,
        image: String?,
    ) {
        emotionRepository.update(
            workspaceId = workspaceId,
            resourceId = resourceId,
            componentId = componentId,
            emotionId = emotionId,
            priority = priority,
            image = image,
        )
    }

}
