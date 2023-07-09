package com.story.platform.api.domain.authentication

import com.story.platform.api.config.auth.AuthContext
import com.story.platform.api.config.auth.RequestAuthContext
import com.story.platform.core.common.model.ApiResponse
import com.story.platform.core.domain.authentication.AuthenticationKeyManager
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthenticationKeyPatchApi(
    private val authenticationKeyManager: AuthenticationKeyManager,
) {

    /**
     * 서비스 인증 키의 정보를 수정합니다
     */
    @PatchMapping("/v1/authentication-keys")
    suspend fun patch(
        @RequestAuthContext authContext: AuthContext,
        @Valid @RequestBody request: AuthenticationKeyPatchApiRequest,
    ): ApiResponse<Nothing?> {
        authenticationKeyManager.patchAuthenticationKey(
            workspaceId = authContext.workspaceId,
            authenticationKey = request.apiKey,
            description = request.description,
            status = request.status,
        )
        return ApiResponse.OK
    }

}
