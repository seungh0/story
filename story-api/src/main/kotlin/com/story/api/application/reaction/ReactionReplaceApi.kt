package com.story.api.application.reaction

import com.story.api.config.auth.AuthContext
import com.story.api.config.auth.RequestAuthContext
import com.story.core.common.model.dto.ApiResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ReactionReplaceApi(
    private val reactionUpsertHandler: ReactionReplaceHandler,
) {

    @PutMapping("/v1/resources/reactions/components/{componentId}/spaces/{spaceId}")
    suspend fun replaceReactions(
        @PathVariable componentId: String,
        @PathVariable spaceId: String,
        @Valid @RequestBody request: ReactionReplaceApiRequest,
        @RequestAuthContext authContext: AuthContext,
    ): ApiResponse<Nothing?> {
        reactionUpsertHandler.replaceReactions(
            workspaceId = authContext.workspaceId,
            componentId = componentId,
            spaceId = spaceId,
            request = request,
            userId = authContext.getRequiredRequestUserId(),
        )
        return ApiResponse.OK
    }

}
