package com.story.api.application.emotion

import jakarta.validation.constraints.NotBlank

data class EmotionCreateRequest(
    @field:NotBlank
    val image: String = "",
    val priority: Long,
)
