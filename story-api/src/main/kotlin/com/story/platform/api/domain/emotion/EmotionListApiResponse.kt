package com.story.platform.api.domain.emotion

import com.story.platform.core.common.model.ContentsWithCursor
import com.story.platform.core.common.model.Cursor
import com.story.platform.core.domain.emotion.EmotionResponse

data class EmotionListApiResponse(
    val emotions: List<EmotionApiResponse>,
    val cursor: Cursor<String>,
) {

    companion object {
        fun of(emotions: ContentsWithCursor<EmotionResponse, String>) = EmotionListApiResponse(
            emotions = emotions.data.map { emotion -> EmotionApiResponse.of(emotion = emotion) },
            cursor = emotions.cursor,
        )
    }

}
