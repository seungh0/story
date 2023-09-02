package com.story.platform.api.domain.reaction

import com.story.platform.api.config.auth.AuthContext
import com.story.platform.api.config.auth.RequestAuthContext
import com.story.platform.core.common.model.dto.ApiResponse
import com.story.platform.core.domain.reaction.ReactionRetriever
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class ReactionRetrieveApi(
    private val reactionRetriever: ReactionRetriever,
) {

    @GetMapping("/v1/reactions/components/{componentId}")
    suspend fun listReaction(
        @PathVariable componentId: String,
        @Valid request: ReactionListApiRequest,
        @RequestAuthContext authContext: AuthContext,
    ): ApiResponse<ReactionListApiResponse> {
        val reactions = reactionRetriever.listReactions(
            workspaceId = authContext.workspaceId,
            componentId = componentId,
            targetIds = request.targetIds,
            accountId = request.accountId,
            optionIds = request.optionIds,
        )
        return ApiResponse.ok(
            ReactionListApiResponse(
                reactions = reactions,
            )
        )
    }

}
