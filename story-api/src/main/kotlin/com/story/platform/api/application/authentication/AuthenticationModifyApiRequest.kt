package com.story.platform.api.application.authentication

import com.story.platform.core.domain.authentication.AuthenticationStatus
import jakarta.validation.constraints.Size

data class AuthenticationModifyApiRequest(
    @field:Size(max = 300)
    val description: String? = null,
    val status: AuthenticationStatus? = null,
)
