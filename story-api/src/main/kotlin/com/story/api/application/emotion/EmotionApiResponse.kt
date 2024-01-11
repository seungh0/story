package com.story.api.application.emotion

import com.story.core.domain.emotion.EmotionResponse

data class EmotionApiResponse(
    val emotionId: String,
    val priority: Long,
    val image: String,
) {

    companion object {
        fun of(emotion: EmotionResponse) = EmotionApiResponse(
            emotionId = emotion.emotionId,
            priority = emotion.priority,
            image = emotion.image,
        )
    }

}
