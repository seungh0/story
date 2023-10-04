package com.story.platform.api.domain.reaction

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

data class ReactionListApiRequest(
    val accountId: String? = null,

    @field:NotEmpty
    @field:Size(max = 20)
    val spaceIds: Set<String> = emptySet(),
)
