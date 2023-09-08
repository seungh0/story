package com.story.platform.api.domain.reaction

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ReactionRemoveApiRequest(
    @field:Size(max = 100)
    @field:NotBlank
    val accountId: String = "",
)
