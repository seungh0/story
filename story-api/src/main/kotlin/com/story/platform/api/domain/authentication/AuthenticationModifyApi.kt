package com.story.platform.api.domain.authentication

import com.story.platform.api.config.auth.AuthContext
import com.story.platform.api.config.auth.RequestAuthContext
import com.story.platform.core.common.model.dto.ApiResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthenticationModifyApi(
    private val authenticationModifyHandler: AuthenticationModifyHandler,
) {

    /**
     * 서비스 인증 키의 정보를 수정합니다
     */
    @PatchMapping("/v1/authentication-keys/{authenticationKey}")
    suspend fun patch(
        @PathVariable authenticationKey: String,
        @RequestAuthContext authContext: AuthContext,
        @Valid @RequestBody request: AuthenticationModifyApiRequest,
    ): ApiResponse<Nothing?> {
        authenticationModifyHandler.patchAuthenticationKey(
            workspaceId = authContext.workspaceId,
            authenticationKey = authenticationKey,
            description = request.description,
            status = request.status,
        )
        return ApiResponse.OK
    }

}
