package com.story.platform.api.domain.authentication

import com.story.platform.core.common.model.dto.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthenticationRetrieveApi(
    private val authenticationRetrieveHandler: AuthenticationRetrieveHandler,
) {

    /**
     * 서비스 인증 키를 조회합니다
     */
    @GetMapping("/v1/authentication/{authenticationKey}")
    suspend fun getAuthentication(
        @PathVariable authenticationKey: String,
    ): ApiResponse<AuthenticationApiResponse> {
        val response = authenticationRetrieveHandler.getAuthentication(authenticationKey = authenticationKey)
        return ApiResponse.ok(response)
    }

}
