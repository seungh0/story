package com.story.platform.api.domain.authentication

import com.story.platform.api.config.auth.AuthContext
import com.story.platform.api.config.auth.RequestAuthContext
import com.story.platform.core.common.model.ApiResponse
import com.story.platform.core.domain.authentication.AuthenticationKeyManager
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthenticationKeyManageApi(
    private val authenticationKeyManager: AuthenticationKeyManager,
) {

    /**
     * 신규 서비스 인증 키를 등록합니다
     */
    @PostMapping("/v1/authentication-keys")
    suspend fun register(
        @RequestAuthContext authContext: AuthContext,
        @Valid @RequestBody request: AuthenticationKeyRegisterApiRequest,
    ): ApiResponse<String> {
        authenticationKeyManager.register(
            workspaceId = authContext.workspaceId,
            apiKey = request.apiKey,
            description = request.description,
        )
        return ApiResponse.OK
    }

    /**
     * 서비스 인증 키의 정보를 수정합니다
     */
    @PatchMapping("/v1/authentication-keys")
    suspend fun modify(
        @RequestAuthContext authContext: AuthContext,
        @Valid @RequestBody request: AuthenticationKeyModifyApiRequest,
    ): ApiResponse<String> {
        authenticationKeyManager.modify(
            workspaceId = authContext.workspaceId,
            apiKey = request.apiKey,
            description = request.description,
            status = request.status,
        )
        return ApiResponse.OK
    }

}
