package com.story.platform.api.domain.reaction

import com.story.platform.core.domain.reaction.ReactionResponse

data class ReactionApiResponse(
    val spaceId: String,
    val emotions: List<ReactionEmotionApiResponse>,
) {

    companion object {
        fun of(reaction: ReactionResponse) = ReactionApiResponse(
            spaceId = reaction.spaceId,
            emotions = reaction.emotions.map { emotion -> ReactionEmotionApiResponse.of(emotion = emotion) },
        )
    }

}
