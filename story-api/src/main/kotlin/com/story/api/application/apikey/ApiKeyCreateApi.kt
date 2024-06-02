package com.story.api.application.apikey

import com.story.api.config.apikey.ApiKeyContext
import com.story.api.config.apikey.RequestApiKey
import com.story.core.common.model.dto.ApiResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ApiKeyCreateApi(
    private val apiKeyCreateHandler: ApiKeyCreateHandler,
) {

    /**
     * 신규 서비스 API 키를 생성합니다
     */
    @PostMapping("/v1/api-keys/{apiKey}")
    suspend fun createApiKey(
        @PathVariable apiKey: String,
        @RequestApiKey authContext: ApiKeyContext,
        @Valid @RequestBody request: ApiKeyCreateRequest,
    ): ApiResponse<Nothing?> {
        apiKeyCreateHandler.createApiKey(
            workspaceId = authContext.workspaceId,
            apiKey = apiKey,
            description = request.description,
        )
        return ApiResponse.OK
    }

}
