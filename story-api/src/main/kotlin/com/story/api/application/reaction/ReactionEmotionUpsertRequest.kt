package com.story.api.application.reaction

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ReactionEmotionUpsertRequest(
    @field:Size(max = 100)
    @field:NotBlank
    val emotionId: String,
)
