package com.story.api.application.reaction

import com.story.core.domain.emotion.Emotion
import com.story.core.domain.reaction.Reaction

data class ReactionListApiResponse(
    val spaceReactions: List<ReactionApiResponse>,
) {

    companion object {
        fun of(reactions: List<Reaction>, emotions: Map<String, Emotion>) = ReactionListApiResponse(
            spaceReactions = reactions.map { reaction -> ReactionApiResponse.of(reaction = reaction, emotions = emotions) }
        )
    }

}
