package com.story.platform.api.domain.authentication

import jakarta.validation.constraints.Size

data class AuthenticationCreateApiRequest(
    @field:Size(max = 300)
    val description: String = "",
)
