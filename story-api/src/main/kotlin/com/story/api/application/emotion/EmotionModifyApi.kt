package com.story.api.application.emotion

import com.story.api.config.auth.AuthContext
import com.story.api.config.auth.RequestAuthContext
import com.story.core.common.model.dto.ApiResponse
import com.story.core.domain.resource.ResourceId
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class EmotionModifyApi(
    private val emotionModifyHandler: EmotionModifyHandler,
) {

    @PatchMapping("/v1/resources/{resourceId}/components/{componentId}/emotions/{emotionId}")
    suspend fun modifyEmotion(
        @PathVariable resourceId: String,
        @PathVariable componentId: String,
        @PathVariable emotionId: String,
        @Valid @RequestBody request: EmotionModifyApiRequest,
        @RequestAuthContext authContext: AuthContext,
    ): ApiResponse<Nothing?> {
        emotionModifyHandler.modifyEmotion(
            workspaceId = authContext.workspaceId,
            resourceId = ResourceId.findByCode(resourceId),
            componentId = componentId,
            emotionId = emotionId,
            request = request,
        )
        return ApiResponse.OK
    }

}