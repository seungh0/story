package com.story.platform.api.domain.authentication

import com.story.platform.api.config.auth.AuthContext
import com.story.platform.api.config.auth.RequestAuthContext
import com.story.platform.core.common.model.dto.ApiResponse
import com.story.platform.core.domain.authentication.AuthenticationKeyManager
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthenticationKeyCreateApi(
    private val authenticationKeyManager: AuthenticationKeyManager,
) {

    /**
     * 신규 서비스 인증 키를 생성합니다
     */
    @PostMapping("/v1/authentication-keys/{authenticationKey}")
    suspend fun create(
        @PathVariable authenticationKey: String,
        @RequestAuthContext authContext: AuthContext,
        @Valid @RequestBody request: AuthenticationKeyCreateApiRequest,
    ): ApiResponse<Nothing?> {
        authenticationKeyManager.createAuthenticationKey(
            workspaceId = authContext.workspaceId,
            authenticationKey = authenticationKey,
            description = request.description,
        )
        return ApiResponse.OK
    }

}
