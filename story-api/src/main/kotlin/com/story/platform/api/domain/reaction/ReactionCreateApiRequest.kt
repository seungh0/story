package com.story.platform.api.domain.reaction

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

data class ReactionCreateApiRequest(
    @field:Size(max = 100)
    @field:NotBlank
    val accountId: String = "",

    @field:NotEmpty
    @field:Size(max = 20)
    val options: Set<ReactionOptionCreateApiRequest> = emptySet(),
)

data class ReactionOptionCreateApiRequest(
    val optionId: String,
)
