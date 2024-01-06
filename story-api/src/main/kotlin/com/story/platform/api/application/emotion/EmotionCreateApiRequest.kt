package com.story.platform.api.application.emotion

import jakarta.validation.constraints.NotBlank

data class EmotionCreateApiRequest(
    @field:NotBlank
    val image: String = "",
    val priority: Long,
)
