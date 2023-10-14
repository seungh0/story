package com.story.platform.core.domain.emotion

import org.springframework.stereotype.Service

@Service
class EmotionCreator(
    private val emotionRepository: EmotionRepository,
) {

    suspend fun createEmotion(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        emotionId: String,
        image: String,
    ) {
        val emotion = Emotion.of(
            workspaceId = workspaceId,
            componentId = componentId,
            spaceId = spaceId,
            emotionId = emotionId,
            image = image,
        )
        emotionRepository.save(emotion)
    }

}
