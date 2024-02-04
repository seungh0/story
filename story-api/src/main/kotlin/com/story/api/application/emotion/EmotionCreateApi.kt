package com.story.api.application.emotion

import com.story.api.config.apikey.ApiKeyContext
import com.story.api.config.apikey.RequestApiKey
import com.story.core.common.model.dto.ApiResponse
import com.story.core.domain.resource.ResourceId
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class EmotionCreateApi(
    private val emotionCreateHandler: EmotionCreateHandler,
) {

    @PostMapping("/v1/resources/{resourceId}/components/{componentId}/emotions/{emotionId}")
    suspend fun createEmotion(
        @PathVariable resourceId: String,
        @PathVariable componentId: String,
        @PathVariable emotionId: String,
        @Valid @RequestBody request: EmotionCreateApiRequest,
        @RequestApiKey authContext: ApiKeyContext,
    ): ApiResponse<Nothing?> {
        emotionCreateHandler.createEmotion(
            workspaceId = authContext.workspaceId,
            resourceId = ResourceId.findByCode(resourceId),
            componentId = componentId,
            emotionId = emotionId,
            request = request,
        )
        return ApiResponse.OK
    }

}
