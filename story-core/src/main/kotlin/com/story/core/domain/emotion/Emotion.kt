package com.story.core.domain.emotion

import com.story.core.domain.emotion.storage.EmotionEntity

data class Emotion(
    val emotionId: String,
    val priority: Long,
    val image: String,
) {

    companion object {
        fun from(emotion: EmotionEntity) = Emotion(
            emotionId = emotion.key.emotionId,
            priority = emotion.priority,
            image = emotion.image,
        )
    }

}
