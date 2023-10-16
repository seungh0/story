package com.story.platform.api.domain.reaction

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

data class ReactionListApiRequest(
    @field:NotEmpty
    @field:Size(max = 20)
    val spaceIds: Set<String> = emptySet(),
    val includeUnselectedEmotions: Boolean = false,
)
