package com.story.api.application.reaction

import com.story.core.domain.emotion.Emotion
import com.story.core.domain.reaction.Reaction
import com.story.core.domain.reaction.ReactionEmotionResponse

data class ReactionApiResponse(
    val spaceId: String,
    val reactions: List<ReactionEmotionApiResponse>,
) {

    companion object {
        fun of(reaction: Reaction, emotions: Map<String, Emotion>): ReactionApiResponse {
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
