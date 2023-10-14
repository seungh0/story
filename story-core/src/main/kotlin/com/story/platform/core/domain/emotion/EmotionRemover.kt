package com.story.platform.core.domain.emotion

import org.springframework.stereotype.Service

@Service
class EmotionRemover(
    private val emotionRepository: EmotionRepository,
) {

    suspend fun removeEmotion(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        emotionId: String,
    ) {
        emotionRepository.deleteById(
            EmotionPrimaryKey(
                workspaceId = workspaceId,
                componentId = componentId,
                spaceId = spaceId,
                emotionId = emotionId,
            )
        )
    }

}
