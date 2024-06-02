package com.story.api.application.emotion

import com.story.api.config.apikey.ApiKeyContext
import com.story.api.config.apikey.RequestApiKey
import com.story.core.common.model.dto.ApiResponse
import com.story.core.domain.resource.ResourceId
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class EmotionRetrieveApi(
    private val emotionRetrieveHandler: EmotionRetrieveHandler,
) {

    @GetMapping("/v1/resources/{resourceId}/components/{componentId}/emotions")
    suspend fun listEmotions(
        @PathVariable resourceId: String,
        @PathVariable componentId: String,
        @RequestApiKey authContext: ApiKeyContext,
    ): ApiResponse<EmotionListResponse> {
        val response = emotionRetrieveHandler.listEmotions(
            workspaceId = authContext.workspaceId,
            resourceId = ResourceId.findByCode(resourceId),
            componentId = componentId,
        )
        return ApiResponse.ok(response)
    }

}
