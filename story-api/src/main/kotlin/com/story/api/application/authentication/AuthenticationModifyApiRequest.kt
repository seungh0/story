package com.story.api.application.authentication

import com.story.core.domain.authentication.AuthenticationStatus
import jakarta.validation.constraints.Size

data class AuthenticationModifyApiRequest(
    @field:Size(max = 300)
    val description: String? = null,
    val status: AuthenticationStatus? = null,
)
