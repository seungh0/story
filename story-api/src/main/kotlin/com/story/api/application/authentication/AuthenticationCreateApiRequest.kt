package com.story.api.application.authentication

import jakarta.validation.constraints.Size

data class AuthenticationCreateApiRequest(
    @field:Size(max = 300)
    val description: String = "",
)
