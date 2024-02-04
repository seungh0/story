package com.story.api.application.apikey

import com.story.core.common.model.dto.ApiResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class ApiKeyRetrieveApi(
    private val apiKeyRetrieveHandler: ApiKeyRetrieveHandler,
) {

    /**
     * 서비스 API-Key를 조회합니다
     */
    @GetMapping("/v1/api-keys/{apiKey}")
    suspend fun getApiKey(
        @PathVariable apiKey: String,
        @Valid request: ApiKeyGetApiRequest,
    ): ApiResponse<ApiKeyApiResponse> {
        val response = apiKeyRetrieveHandler.getApiKey(
            key = apiKey,
            filterStatus = request.filterStatus
        )
        return ApiResponse.ok(response)
    }

}
