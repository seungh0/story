package com.story.platform.api.domain.post

import jakarta.validation.constraints.NotBlank

data class RegisterPostApiRequest(
    @field:NotBlank
    val accountId: String = "",

    @field:NotBlank
    val title: String = "",

    @field:NotBlank
    val content: String = "",

    val extraJson: String? = null,
)
