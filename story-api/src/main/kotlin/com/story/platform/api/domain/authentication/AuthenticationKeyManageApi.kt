package com.story.platform.api.domain.authentication

import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.common.model.ApiResponse
import com.story.platform.core.domain.authentication.AuthenticationKeyManager
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
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
    @PostMapping("/v1/service/{serviceType}/authentication/key")
    suspend fun register(
        @PathVariable serviceType: ServiceType,
        @Valid @RequestBody request: AuthenticationKeyRegisterApiRequest,
    ): ApiResponse<String> {
        authenticationKeyManager.register(
            serviceType = serviceType,
            apiKey = request.apiKey,
            description = request.description,
        )
        return ApiResponse.OK
    }

    /**
     * 서비스 인증 키의 정보를 수정합니다
     */
    @PatchMapping("/v1/service/{serviceType}/authentication/key")
    suspend fun modify(
        @PathVariable serviceType: ServiceType,
        @Valid @RequestBody request: AuthenticationKeyModifyApiRequest,
    ): ApiResponse<String> {
        authenticationKeyManager.modify(
            serviceType = serviceType,
            apiKey = request.apiKey,
            description = request.description,
            status = request.status,
        )
        return ApiResponse.OK
    }

}
