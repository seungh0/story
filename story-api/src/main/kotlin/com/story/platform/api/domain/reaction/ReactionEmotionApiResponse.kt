package com.story.platform.api.domain.reaction

import com.story.platform.core.domain.emotion.EmotionResponse
import com.story.platform.core.domain.reaction.ReactionEmotionResponse

data class ReactionEmotionApiResponse(
    val emotionId: String,
    val image: String,
    val count: Long,
    val reactedByMe: Boolean,
) {

    companion object {
        fun of(
            reactionEmotion: ReactionEmotionResponse,
            emotion: EmotionResponse,
        ) = ReactionEmotionApiResponse(
            emotionId = reactionEmotion.emotionId,
            image = emotion.image,
            count = reactionEmotion.count,
            reactedByMe = reactionEmotion.reactedByMe,
        )
    }

}
