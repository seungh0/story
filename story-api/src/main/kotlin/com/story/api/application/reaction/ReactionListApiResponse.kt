package com.story.api.application.reaction

import com.story.core.domain.emotion.EmotionResponse
import com.story.core.domain.reaction.ReactionResponse

data class ReactionListApiResponse(
    val spaceReactions: List<ReactionApiResponse>,
) {

    companion object {
        fun of(reactions: List<ReactionResponse>, emotions: Map<String, EmotionResponse>) = ReactionListApiResponse(
            spaceReactions = reactions.map { reaction -> ReactionApiResponse.of(reaction = reaction, emotions = emotions) }
        )
    }

}
