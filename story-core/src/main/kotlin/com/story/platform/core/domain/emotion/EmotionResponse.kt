package com.story.platform.core.domain.emotion

data class EmotionResponse(
    val emotionId: String,
    val priority: Long,
    val image: String,
) {

    companion object {
        fun of(emotion: Emotion) = EmotionResponse(
            emotionId = emotion.key.emotionId,
            priority = emotion.priority,
            image = emotion.image,
        )
    }

}
