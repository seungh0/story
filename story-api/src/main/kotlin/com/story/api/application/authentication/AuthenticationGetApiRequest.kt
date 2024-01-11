package com.story.api.application.authentication

import com.story.core.domain.authentication.AuthenticationStatus

data class AuthenticationGetApiRequest(
    val filterStatus: AuthenticationStatus = AuthenticationStatus.ENABLED,
)
