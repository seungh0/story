package com.story.platform.api.domain.authentication

import com.story.platform.core.common.spring.HandlerAdapter
import com.story.platform.core.domain.authentication.AuthenticationKeyResponse
import com.story.platform.core.domain.authentication.AuthenticationKeyRetriever

@HandlerAdapter
class AuthenticationKeyRetrieveHandler(
    private val authenticationKeyManager: AuthenticationKeyRetriever,
) {

    suspend fun getAuthenticationKey(
        authenticationKey: String,
    ): AuthenticationKeyResponse {
        return authenticationKeyManager.getAuthenticationKey(authenticationKey = authenticationKey)
    }

}
