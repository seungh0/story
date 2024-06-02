package com.story.api.application.emotion

import com.story.core.common.model.Slice
import com.story.core.domain.emotion.Emotion

data class EmotionListResponse(
    val emotions: List<EmotionResponse>,
) {

    companion object {
        fun of(emotions: Slice<Emotion, String>) = EmotionListResponse(
            emotions = emotions.data.asSequence()
                .sortedBy { emotion -> emotion.priority }
                .map { emotion -> EmotionResponse.of(emotion = emotion) }
                .toList(),
        )
    }

}
