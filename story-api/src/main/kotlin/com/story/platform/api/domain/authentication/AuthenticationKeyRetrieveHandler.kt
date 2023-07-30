package com.story.platform.api.domain.authentication

import com.story.platform.core.domain.authentication.AuthenticationKeyRetriever
import com.story.platform.core.domain.authentication.AuthenticationResponse
import org.springframework.stereotype.Service

@Service
class AuthenticationKeyRetrieveHandler(
    private val authenticationKeyManager: AuthenticationKeyRetriever,
) {

    suspend fun getAuthenticationKey(
        authenticationKey: String,
    ): AuthenticationResponse {
        return authenticationKeyManager.getAuthenticationKey(authenticationKey = authenticationKey)
    }

}
