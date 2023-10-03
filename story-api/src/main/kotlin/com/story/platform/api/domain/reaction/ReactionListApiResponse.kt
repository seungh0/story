package com.story.platform.api.domain.reaction

import com.story.platform.core.domain.reaction.ReactionResponse

data class ReactionListApiResponse(
    val reactions: List<ReactionApiResponse>,
) {

    companion object {
        fun of(reactions: List<ReactionResponse>) = ReactionListApiResponse(
            reactions = reactions.map { reaction -> ReactionApiResponse.of(reaction = reaction) }
        )
    }

}
