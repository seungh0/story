package com.story.platform.api.domain.authentication

import com.story.platform.core.domain.authentication.AuthenticationKeyStatus

data class AuthenticationKeyModifyApiRequest(
    val description: String?,
    val status: AuthenticationKeyStatus?,
)
