package com.story.api.application.reaction

import com.story.core.domain.emotion.Emotion
import com.story.core.domain.reaction.ReactionWithEmotionCount

data class ReactionListResponse(
    val spaceReactions: List<ReactionResponse>,
) {

    companion object {
        fun of(reactions: List<ReactionWithEmotionCount>, emotions: Map<String, Emotion>) = ReactionListResponse(
            spaceReactions = reactions.map { reaction -> ReactionResponse.of(reaction = reaction, emotions = emotions) }
        )
    }

}
