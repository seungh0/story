package com.story.platform.api.application.emotion

import com.story.platform.api.config.auth.AuthContext
import com.story.platform.api.config.auth.RequestAuthContext
import com.story.platform.core.common.model.dto.ApiResponse
import com.story.platform.core.domain.resource.ResourceId
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
        @RequestAuthContext authContext: AuthContext,
    ): ApiResponse<EmotionListApiResponse> {
        val response = emotionRetrieveHandler.listEmotions(
            workspaceId = authContext.workspaceId,
            resourceId = ResourceId.findByCode(resourceId),
            componentId = componentId,
        )
        return ApiResponse.ok(response)
    }

}
