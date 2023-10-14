package com.story.platform.api.domain.reaction

import com.story.platform.core.domain.emotion.EmotionResponse
import com.story.platform.core.domain.reaction.ReactionResponse

data class ReactionApiResponse(
    val spaceId: String,
    val emotions: List<ReactionEmotionApiResponse>,
) {

    companion object {
        fun of(reaction: ReactionResponse, emotions: Map<String, EmotionResponse>) = ReactionApiResponse(
            spaceId = reaction.spaceId,
            emotions = reaction.emotions.asSequence()
                .filter { reactionEmotion -> emotions[reactionEmotion.emotionId] != null }
                .map { reactionEmotion ->
                    ReactionEmotionApiResponse.of(
                        reactionEmotion = reactionEmotion,
                        emotion = emotions[reactionEmotion.emotionId]!!,
                    )
                }
                .toList(),
        )
    }

}
