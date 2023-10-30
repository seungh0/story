package com.story.platform.api.domain.authentication

import com.story.platform.core.domain.authentication.AuthenticationStatus

data class AuthenticationGetApiRequest(
    val filterStatus: AuthenticationStatus = AuthenticationStatus.ENABLED,
)
