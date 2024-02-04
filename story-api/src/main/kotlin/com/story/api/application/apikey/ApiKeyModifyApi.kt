package com.story.api.application.apikey

import com.story.api.config.apikey.ApiKeyContext
import com.story.api.config.apikey.RequestApiKey
import com.story.core.common.model.dto.ApiResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ApiKeyModifyApi(
    private val apiKeyModifyHandler: ApiKeyModifyHandler,
) {

    /**
     * 서비스 API-Key의 정보를 수정합니다
     */
    @PatchMapping("/v1/api-keys/{apiKey}")
    suspend fun patchApiKey(
        @PathVariable apiKey: String,
        @RequestApiKey authContext: ApiKeyContext,
        @Valid @RequestBody request: ApiKeyModifyApiRequest,
    ): ApiResponse<Nothing?> {
        apiKeyModifyHandler.patchApiKey(
            workspaceId = authContext.workspaceId,
            apiKey = apiKey,
            description = request.description,
            status = request.status,
        )
        return ApiResponse.OK
    }

}
