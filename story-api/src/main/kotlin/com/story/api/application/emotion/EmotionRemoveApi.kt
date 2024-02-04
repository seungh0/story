package com.story.api.application.emotion

import com.story.api.config.apikey.ApiKeyContext
import com.story.api.config.apikey.RequestApiKey
import com.story.core.common.model.dto.ApiResponse
import com.story.core.domain.resource.ResourceId
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class EmotionRemoveApi(
    private val emotionRemoveHandler: EmotionRemoveHandler,
) {

    @DeleteMapping("/v1/resources/{resourceId}/components/{componentId}/emotions/{emotionId}")
    suspend fun removeEmotion(
        @PathVariable resourceId: String,
        @PathVariable componentId: String,
        @PathVariable emotionId: String,
        @RequestApiKey authContext: ApiKeyContext,
    ): ApiResponse<Nothing?> {
        emotionRemoveHandler.removeEmotion(
            workspaceId = authContext.workspaceId,
            resourceId = ResourceId.findByCode(resourceId),
            componentId = componentId,
            emotionId = emotionId,
        )
        return ApiResponse.OK
    }

}
