package com.story.platform.api.domain.emotion

import com.story.platform.core.common.model.Slice
import com.story.platform.core.domain.emotion.EmotionResponse

data class EmotionListApiResponse(
    val emotions: List<EmotionApiResponse>,
) {

    companion object {
        fun of(emotions: Slice<EmotionResponse, String>) = EmotionListApiResponse(
            emotions = emotions.data.asSequence()
                .sortedBy { emotion -> emotion.priority }
                .map { emotion -> EmotionApiResponse.of(emotion = emotion) }
                .toList(),
        )
    }

}
