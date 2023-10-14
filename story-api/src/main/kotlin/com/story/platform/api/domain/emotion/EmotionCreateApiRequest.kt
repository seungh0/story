package com.story.platform.api.domain.emotion

import jakarta.validation.constraints.NotBlank

data class EmotionCreateApiRequest(
    @field:NotBlank
    val image: String = "",
)
