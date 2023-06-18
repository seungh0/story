package com.story.platform.api.domain.authentication

import com.story.platform.core.common.error.NotFoundException
import com.story.platform.core.common.error.UnAuthorizedException
import com.story.platform.core.common.utils.getApiKey
import com.story.platform.core.domain.authentication.AuthenticationKeyRetriever
import com.story.platform.core.domain.authentication.AuthenticationResponse
import org.springframework.stereotype.Service
import org.springframework.web.server.ServerWebExchange

@Service
class AuthenticationHandler(
    private val authenticationKeyRetriever: AuthenticationKeyRetriever,
) {

    suspend fun handleAuthentication(serverWebExchange: ServerWebExchange): AuthenticationResponse {
        val apiKey = serverWebExchange.getApiKey()
            ?: throw UnAuthorizedException("인증 헤더(X-Story-Api-Key)가 비어있습니다")

        try {
            val authentication = authenticationKeyRetriever.getAuthenticationKey(apiKey = apiKey)
            if (!authentication.isActivated()) {
                throw UnAuthorizedException("[서비스(${authentication.serviceType})] 사용할 수 없는 인증 키(${authentication.apiKey})입니다. 현재 상태: ${authentication.status}")
            }
            return authentication
        } catch (exception: NotFoundException) {
            throw UnAuthorizedException("apiKey($apiKey)는 등록된 인증 키가 아닙니다")
        }
    }

}
