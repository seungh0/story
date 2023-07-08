package com.story.platform.api.domain.authentication

import com.story.platform.core.domain.authentication.AuthenticationKeyStatus
import jakarta.validation.constraints.NotBlank

data class AuthenticationKeyPatchApiRequest(
    @field:NotBlank
    val apiKey: String = "",
    val description: String?,
    val status: AuthenticationKeyStatus?,
)
