package com.story.platform.api.domain.authentication

import com.story.platform.core.common.model.ApiResponse
import com.story.platform.core.domain.authentication.AuthenticationKeyRetriever
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthenticationKeyRetrieveApi(
    private val authenticationKeyManager: AuthenticationKeyRetriever,
) {

    /**
     * 서비스 인증 키를 조회합니다
     */
    @GetMapping("/v1/authentication/key")
    suspend fun getAuthenticationKey(
        @RequestHeader("X-Story-Api-Key") apiKey: String,
    ): ApiResponse<AuthenticationKeyApiResponse> {
        val authenticationKey = authenticationKeyManager.getAuthenticationKey(
            apiKey = apiKey,
        )
        return ApiResponse.success(AuthenticationKeyApiResponse.of(authenticationKey))
    }

}
