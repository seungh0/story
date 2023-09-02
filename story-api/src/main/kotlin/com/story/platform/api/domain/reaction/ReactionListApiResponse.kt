package com.story.platform.api.domain.reaction

import com.story.platform.core.domain.reaction.ReactionResponse

data class ReactionListApiResponse(
    val reactions: List<ReactionResponse>,
)
