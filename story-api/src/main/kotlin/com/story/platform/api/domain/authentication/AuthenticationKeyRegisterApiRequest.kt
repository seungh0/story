package com.story.platform.api.domain.authentication

import jakarta.validation.constraints.NotBlank

data class AuthenticationKeyRegisterApiRequest(
    @field:NotBlank
    val apiKey: String = "",
    val description: String = "",
)
