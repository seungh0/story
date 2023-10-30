package com.story.platform.api.domain.authentication

import com.story.platform.core.common.annotation.HandlerAdapter
import com.story.platform.core.domain.authentication.AuthenticationKeyNotExistsException
import com.story.platform.core.domain.authentication.AuthenticationRetriever
import com.story.platform.core.domain.authentication.AuthenticationStatus

@HandlerAdapter
class AuthenticationRetrieveHandler(
    private val authenticationKeyManager: AuthenticationRetriever,
) {

    suspend fun getAuthentication(
        authenticationKey: String,
        filterStatus: AuthenticationStatus?,
    ): AuthenticationApiResponse {
        val authentication = authenticationKeyManager.getAuthentication(authenticationKey = authenticationKey)
        if (filterStatus != null && authentication.status != filterStatus) {
            throw AuthenticationKeyNotExistsException(message = "요청한 상태($filterStatus)가 아닌 인증 키($authenticationKey) 입니다. 현재 상태: ${authentication.status}")
        }
        return AuthenticationApiResponse.of(authenticationKey = authentication)
    }

}
