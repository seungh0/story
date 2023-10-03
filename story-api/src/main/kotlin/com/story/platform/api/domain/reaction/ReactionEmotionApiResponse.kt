package com.story.platform.api.domain.reaction

import com.story.platform.core.domain.reaction.ReactionEmotionResponse

data class ReactionEmotionApiResponse(
    val emotionId: String,
    val count: Long,
    val reactedByMe: Boolean,
) {

    companion object {
        fun of(
            emotion: ReactionEmotionResponse,
        ) = ReactionEmotionApiResponse(
            emotionId = emotion.emotionId,
            count = emotion.count,
            reactedByMe = emotion.reactedByMe,
        )
    }

}
