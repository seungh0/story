package com.story.api.application.reaction

import com.story.api.application.emotion.EmotionResponse
import com.story.core.domain.emotion.Emotion
import com.story.core.domain.reaction.ReactionEmotion

data class ReactionEmotionResponse(
    val emotion: EmotionResponse,
    val count: Long,
    val reactedByMe: Boolean,
) {

    companion object {
        fun of(
            reactionEmotion: ReactionEmotion,
            emotion: Emotion,
        ) = ReactionEmotionResponse(
            count = reactionEmotion.count,
            reactedByMe = reactionEmotion.reactedByMe,
            emotion = EmotionResponse.of(emotion = emotion)
        )
    }

}
