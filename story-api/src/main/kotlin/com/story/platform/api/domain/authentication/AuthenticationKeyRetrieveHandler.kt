package com.story.platform.api.domain.authentication

import com.story.platform.core.domain.authentication.AuthenticationKeyResponse
import com.story.platform.core.domain.authentication.AuthenticationKeyRetriever
import org.springframework.stereotype.Service

@Service
class AuthenticationKeyRetrieveHandler(
    private val authenticationKeyManager: AuthenticationKeyRetriever,
) {

    suspend fun getAuthenticationKey(
        authenticationKey: String,
    ): AuthenticationKeyResponse {
        return authenticationKeyManager.getAuthenticationKey(authenticationKey = authenticationKey)
    }

}
