package com.story.platform.api.domain.authentication

import com.story.platform.core.domain.authentication.AuthenticationStatus

data class AuthenticationModifyApiRequest(
    val description: String?,
    val status: AuthenticationStatus?,
)
