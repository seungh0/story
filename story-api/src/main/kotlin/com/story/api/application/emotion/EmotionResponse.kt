package com.story.api.application.emotion

import com.story.core.domain.emotion.Emotion

data class EmotionResponse(
    val emotionId: String,
    val priority: Long,
    val image: String,
) {

    companion object {
        fun of(emotion: Emotion) = EmotionResponse(
            emotionId = emotion.emotionId,
            priority = emotion.priority,
            image = emotion.image,
        )
    }

}
