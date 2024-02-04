package com.story.api.application.reaction

import com.story.api.config.apikey.ApiKeyContext
import com.story.api.config.apikey.RequestApiKey
import com.story.core.common.model.dto.ApiResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class ReactionRetrieveApi(
    private val reactionRetrieveHandler: ReactionRetrieveHandler,
) {

    @GetMapping("/v1/resources/reactions/components/{componentId}/spaces/{spaceId}")
    suspend fun getReaction(
        @PathVariable componentId: String,
        @PathVariable spaceId: String,
        @Valid request: ReactionGetApiRequest,
        @RequestApiKey authContext: ApiKeyContext,
    ): ApiResponse<ReactionApiResponse> {
        val response = reactionRetrieveHandler.getReaction(
            workspaceId = authContext.workspaceId,
            componentId = componentId,
            spaceId = spaceId,
            request = request,
            requestUserId = authContext.requestUserId,
        )
        return ApiResponse.ok(response)
    }

    @GetMapping("/v1/resources/reactions/components/{componentId}/spaces")
    suspend fun listReactions(
        @PathVariable componentId: String,
        @Valid request: ReactionListApiRequest,
        @RequestApiKey authContext: ApiKeyContext,
    ): ApiResponse<ReactionListApiResponse> {
        val response = reactionRetrieveHandler.listReactions(
            workspaceId = authContext.workspaceId,
            componentId = componentId,
            request = request,
            requestUserId = authContext.requestUserId,
        )
        return ApiResponse.ok(response)
    }

}
