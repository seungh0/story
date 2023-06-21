package com.story.platform.api.domain.authentication

import com.story.platform.core.common.enums.HttpHeaderType
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
            ?: throw UnAuthorizedException("인증 헤더(${HttpHeaderType.X_STORY_AUTHENTICATION_KEY.header})가 비어있습니다")

        try {
            val authentication = authenticationKeyRetriever.getAuthenticationKey(authenticationKey = apiKey)
            if (!authentication.isActivated()) {
                throw UnAuthorizedException("사용할 수 없는 인증 키(${authentication.authenticationKey})입니다. [워크스페이스(${authentication.workspaceId}) 현재 상태: ${authentication.status}]")
            }
            return authentication
        } catch (exception: NotFoundException) {
            throw UnAuthorizedException("등록되지 않은 인증키($apiKey)입니다")
        }
    }

}
