package com.story.platform.core.domain.emotion

data class EmotionResponse(
    val emotionId: String,
    val image: String,
) {

    companion object {
        fun of(emotion: Emotion) = EmotionResponse(
            emotionId = emotion.key.emotionId,
            image = emotion.image,
        )
    }

}
