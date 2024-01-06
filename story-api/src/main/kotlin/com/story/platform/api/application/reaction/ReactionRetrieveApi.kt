package com.story.platform.api.application.reaction

import com.story.platform.api.config.auth.AuthContext
import com.story.platform.api.config.auth.RequestAuthContext
import com.story.platform.core.common.model.dto.ApiResponse
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
        @RequestAuthContext authContext: AuthContext,
    ): ApiResponse<ReactionApiResponse> {
        val response = reactionRetrieveHandler.getReaction(
            workspaceId = authContext.workspaceId,
            componentId = componentId,
            spaceId = spaceId,
            request = request,
            requestAccountId = authContext.requestAccountId,
        )
        return ApiResponse.ok(response)
    }

    @GetMapping("/v1/resources/reactions/components/{componentId}/spaces")
    suspend fun listReactions(
        @PathVariable componentId: String,
        @Valid request: ReactionListApiRequest,
        @RequestAuthContext authContext: AuthContext,
    ): ApiResponse<ReactionListApiResponse> {
        val response = reactionRetrieveHandler.listReactions(
            workspaceId = authContext.workspaceId,
            componentId = componentId,
            request = request,
            requestAccountId = authContext.requestAccountId,
        )
        return ApiResponse.ok(response)
    }

}
