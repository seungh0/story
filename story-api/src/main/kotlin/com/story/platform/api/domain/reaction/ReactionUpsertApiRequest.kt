package com.story.platform.api.domain.reaction

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

data class ReactionUpsertApiRequest(
    @field:NotEmpty
    @field:Size(max = 20)
    val emotions: Set<ReactionEmotionUpsertApiRequest> = emptySet(),
)
