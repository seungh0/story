package com.story.core.domain.emotion

data class Emotion(
    val emotionId: String,
    val priority: Long,
    val image: String,
) {

    companion object {
        fun of(emotion: EmotionEntity) = Emotion(
            emotionId = emotion.key.emotionId,
            priority = emotion.priority,
            image = emotion.image,
        )
    }

}
