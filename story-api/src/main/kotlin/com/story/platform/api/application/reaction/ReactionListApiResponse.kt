package com.story.platform.api.application.reaction

import com.story.platform.core.domain.emotion.EmotionResponse
import com.story.platform.core.domain.reaction.ReactionResponse

data class ReactionListApiResponse(
    val spaceReactions: List<ReactionApiResponse>,
) {

    companion object {
        fun of(reactions: List<ReactionResponse>, emotions: Map<String, EmotionResponse>) = ReactionListApiResponse(
            spaceReactions = reactions.map { reaction -> ReactionApiResponse.of(reaction = reaction, emotions = emotions) }
        )
    }

}
