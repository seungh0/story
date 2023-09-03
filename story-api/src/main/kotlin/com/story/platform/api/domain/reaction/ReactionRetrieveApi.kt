package com.story.platform.api.domain.reaction

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

    @GetMapping("/v1/reactions/components/{componentId}/targets")
    suspend fun listReaction(
        @PathVariable componentId: String,
        @Valid request: ReactionListApiRequest,
        @RequestAuthContext authContext: AuthContext,
    ): ApiResponse<ReactionListApiResponse> {
        val response = reactionRetrieveHandler.listReactions(
            workspaceId = authContext.workspaceId,
            componentId = componentId,
            request = request,
        )
        return ApiResponse.ok(response)
    }

}
