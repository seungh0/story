package com.story.platform.api.domain.authentication

import com.story.platform.core.common.spring.HandlerAdapter
import com.story.platform.core.domain.authentication.AuthenticationRetriever

@HandlerAdapter
class AuthenticationRetrieveHandler(
    private val authenticationKeyManager: AuthenticationRetriever,
) {

    suspend fun getAuthenticationKey(
        apiKey: String,
    ): AuthenticationApiResponse {
        val authentication = authenticationKeyManager.getAuthenticationKey(authenticationKey = apiKey)
        return AuthenticationApiResponse.of(authenticationKey = authentication)
    }

}
