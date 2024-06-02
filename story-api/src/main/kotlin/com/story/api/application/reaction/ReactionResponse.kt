package com.story.api.application.reaction

import com.story.core.domain.emotion.Emotion
import com.story.core.domain.reaction.ReactionEmotion
import com.story.core.domain.reaction.ReactionWithEmotionCount

data class ReactionResponse(
    val spaceId: String,
    val reactions: List<ReactionEmotionResponse>,
) {

    companion object {
        fun of(reaction: ReactionWithEmotionCount, emotions: Map<String, Emotion>): ReactionResponse {
            val reactionGroupByEmotionId = reaction.emotions.associateBy { emotion -> emotion.emotionId }
            return ReactionResponse(
                spaceId = reaction.spaceId,
                reactions = emotions.values.asSequence()
                    .map { emotion ->
                        ReactionEmotionResponse.of(
                            reactionEmotion = reactionGroupByEmotionId[emotion.emotionId]
                                ?: ReactionEmotion.zero(emotionId = emotion.emotionId),
                            emotion = emotion,
                        )
                    }
                    .sortedBy { reactionEmotion -> reactionEmotion.emotion.priority }
                    .toList()
            )
        }
    }

}
