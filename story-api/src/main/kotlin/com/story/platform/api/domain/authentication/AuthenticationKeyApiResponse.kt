package com.story.platform.api.domain.authentication

import com.story.platform.core.domain.authentication.AuthenticationKeyResponse
import com.story.platform.core.domain.authentication.AuthenticationKeyStatus

data class AuthenticationKeyApiResponse(
    val workspaceId: String,
    val apiKey: String,
    val status: AuthenticationKeyStatus,
    val description: String,
) {

    companion object {
        fun of(
            authenticationKey: AuthenticationKeyResponse,
        ) = AuthenticationKeyApiResponse(
            workspaceId = authenticationKey.workspaceId,
            apiKey = authenticationKey.authenticationKey,
            status = authenticationKey.status,
            description = authenticationKey.description,
        )
    }

}
