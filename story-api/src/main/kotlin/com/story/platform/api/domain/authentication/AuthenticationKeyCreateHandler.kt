package com.story.platform.api.domain.authentication

import com.story.platform.core.domain.authentication.AuthenticationKeyCreator
import org.springframework.stereotype.Service

@Service
class AuthenticationKeyCreateHandler(
    private val authenticationKeyCreator: AuthenticationKeyCreator,
) {

    suspend fun createAuthenticationKey(
        workspaceId: String,
        authenticationKey: String,
        description: String,
    ) {
        authenticationKeyCreator.createAuthenticationKey(
            workspaceId = workspaceId,
            authenticationKey = authenticationKey,
            description = description,
        )
    }

}
