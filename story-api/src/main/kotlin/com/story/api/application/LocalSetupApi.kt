package com.story.api.application

import com.story.core.common.model.dto.ApiResponse
import com.story.core.domain.apikey.ApiKeyWriteRepository
import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Profile("local")
@RestController
class LocalSetupApi(
    private val apiKeyWriteRepository: ApiKeyWriteRepository,
) {

    @PostMapping("/setup")
    suspend fun setup(
        @RequestParam workspaceId: String,
        @RequestParam apiKey: String,
    ): ApiResponse<Nothing?> {
        apiKeyWriteRepository.create(
            key = apiKey,
            workspaceId = workspaceId,
            description = "story",
        )
        return ApiResponse.OK
    }

}
