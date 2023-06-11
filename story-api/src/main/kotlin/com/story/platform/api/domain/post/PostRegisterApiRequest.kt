package com.story.platform.api.domain.post

import jakarta.validation.constraints.NotBlank

data class PostRegisterApiRequest(
    @field:NotBlank
    val accountId: String = "",

    @field:NotBlank
    val title: String = "",

    val content: String = "",

    val extraJson: String? = null,
)
