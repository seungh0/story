package com.story.platform.api.domain.reaction

import com.story.platform.core.domain.reaction.ReactionResponse

data class ReactionApiResponse(
    val workspaceId: String,
    val componentId: String,
    val spaceId: String,
    val emotions: List<ReactionEmotionApiResponse>,
) {

    companion object {
        fun of(reaction: ReactionResponse) = ReactionApiResponse(
            workspaceId = reaction.workspaceId,
            componentId = reaction.componentId,
            spaceId = reaction.spaceId,
            emotions = reaction.emotions.map { emotion -> ReactionEmotionApiResponse.of(emotion = emotion) },
        )
    }

}
