package com.story.platform.api.domain.authentication

import com.story.platform.core.domain.authentication.AuthenticationResponse
import com.story.platform.core.domain.authentication.AuthenticationStatus

data class AuthenticationApiResponse(
    val apiKey: String,
    val status: AuthenticationStatus,
    val description: String,
) {

    companion object {
        fun of(
            authenticationKey: AuthenticationResponse,
        ) = AuthenticationApiResponse(
            apiKey = authenticationKey.authenticationKey,
            status = authenticationKey.status,
            description = authenticationKey.description,
        )
    }

}
