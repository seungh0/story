package com.story.api.application.authentication

import com.story.core.common.model.dto.ApiResponse
import jakarta.validation.Valid
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
        @Valid request: AuthenticationGetApiRequest,
    ): ApiResponse<AuthenticationApiResponse> {
        val response = authenticationRetrieveHandler.getAuthentication(
            authenticationKey = authenticationKey,
            filterStatus = request.filterStatus
        )
        return ApiResponse.ok(response)
    }

}
