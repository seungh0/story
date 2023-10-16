package com.story.platform.api.domain.emotion

import com.story.platform.core.domain.emotion.EmotionResponse

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
