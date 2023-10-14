package com.story.platform.core.domain.emotion

import com.story.platform.core.domain.resource.ResourceId
import org.springframework.stereotype.Service

@Service
class EmotionCreator(
    private val emotionRepository: EmotionRepository,
) {

    suspend fun createEmotion(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        emotionId: String,
        image: String,
    ) {
        val emotion = Emotion.of(
            workspaceId = workspaceId,
            resourceId = resourceId,
            componentId = componentId,
            emotionId = emotionId,
            image = image,
        )
        emotionRepository.save(emotion)
    }

}
