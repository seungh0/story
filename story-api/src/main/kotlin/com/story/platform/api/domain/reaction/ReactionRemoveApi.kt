package com.story.platform.api.domain.reaction

import com.story.platform.api.config.auth.AuthContext
import com.story.platform.api.config.auth.RequestAuthContext
import com.story.platform.core.common.model.dto.ApiResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class ReactionRemoveApi(
    private val reactionRemoveHandler: ReactionRemoveHandler,
) {

    @DeleteMapping("/v1/resources/reactions/components/{componentId}/spaces/{spaceId}")
    suspend fun removeReaction(
        @PathVariable componentId: String,
        @PathVariable spaceId: String,
        @Valid request: ReactionRemoveApiRequest,
        @RequestAuthContext authContext: AuthContext,
    ): ApiResponse<Nothing?> {
        reactionRemoveHandler.removeReaction(
            workspaceId = authContext.workspaceId,
            componentId = componentId,
            spaceId = spaceId,
            accountId = request.accountId,
        )
        return ApiResponse.OK
    }

}
