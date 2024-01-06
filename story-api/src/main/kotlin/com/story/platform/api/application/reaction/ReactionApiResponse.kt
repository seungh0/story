package com.story.platform.api.application.reaction

import com.story.platform.core.domain.emotion.EmotionResponse
import com.story.platform.core.domain.reaction.ReactionEmotionResponse
import com.story.platform.core.domain.reaction.ReactionResponse

data class ReactionApiResponse(
    val spaceId: String,
    val reactions: List<ReactionEmotionApiResponse>,
) {

    companion object {
        fun of(reaction: ReactionResponse, emotions: Map<String, EmotionResponse>): ReactionApiResponse {
            val reactionGroupByEmotionId = reaction.emotions.associateBy { emotion -> emotion.emotionId }
            return ReactionApiResponse(
                spaceId = reaction.spaceId,
                reactions = emotions.values.asSequence()
                    .map { emotion ->
                        ReactionEmotionApiResponse.of(
                            reactionEmotion = reactionGroupByEmotionId[emotion.emotionId]
                                ?: ReactionEmotionResponse.zero(emotionId = emotion.emotionId),
                            emotion = emotion,
                        )
                    }
                    .sortedBy { reactionEmotion -> reactionEmotion.emotion.priority }
                    .toList()
            )
        }
    }

}
