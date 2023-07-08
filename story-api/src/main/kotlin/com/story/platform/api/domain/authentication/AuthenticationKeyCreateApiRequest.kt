package com.story.platform.api.domain.authentication

import jakarta.validation.constraints.NotBlank

data class AuthenticationKeyCreateApiRequest(
    @field:NotBlank
    val apiKey: String = "",
    val description: String = "",
)
