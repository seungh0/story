package com.story.platform.core.domain.emotion

data class EmotionResponse(
    val workspaceId: String,
    val componentId: String,
    val emotionId: String,
    val image: String,
) {

    companion object {
        fun of(emotion: Emotion) = EmotionResponse(
            workspaceId = emotion.key.workspaceId,
            componentId = emotion.key.componentId,
            emotionId = emotion.key.emotionId,
            image = emotion.image,
        )
    }

}
