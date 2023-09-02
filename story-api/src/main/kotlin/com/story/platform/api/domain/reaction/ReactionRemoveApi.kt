package com.story.platform.api.domain.reaction

import com.story.platform.api.config.auth.AuthContext
import com.story.platform.api.config.auth.RequestAuthContext
import com.story.platform.core.common.model.dto.ApiResponse
import com.story.platform.core.domain.reaction.ReactionRemover
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class ReactionRemoveApi(
    private val reactionRemover: ReactionRemover,
) {

    @DeleteMapping("/v1/reactions/components/{componentId}/targets/{targetId}")
    suspend fun removeReaction(
        @PathVariable componentId: String,
        @PathVariable targetId: String,
        @Valid request: ReactionRemoveApiRequest,
        @RequestAuthContext authContext: AuthContext,
    ): ApiResponse<Nothing?> {
        reactionRemover.remove(
            workspaceId = authContext.workspaceId,
            componentId = componentId,
            targetId = targetId,
            accountId = request.accountId,
        )
        return ApiResponse.OK
    }

}
