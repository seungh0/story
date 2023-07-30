package com.story.platform.api.domain.authentication

import com.story.platform.core.common.model.dto.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthenticationKeyRetrieveApi(
    private val authenticationKeyRetrieveHandler: AuthenticationKeyRetrieveHandler,
) {

    /**
     * 서비스 인증 키를 조회합니다
     */
    @GetMapping("/v1/authentication-keys/{authenticationKey}")
    suspend fun getAuthenticationKey(
        @PathVariable authenticationKey: String,
    ): ApiResponse<AuthenticationKeyApiResponse> {
        val response = authenticationKeyRetrieveHandler.getAuthenticationKey(authenticationKey = authenticationKey)
        return ApiResponse.ok(AuthenticationKeyApiResponse.of(response))
    }

}
