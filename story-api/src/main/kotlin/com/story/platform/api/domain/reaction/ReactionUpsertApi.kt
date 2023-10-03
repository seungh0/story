package com.story.platform.api.domain.reaction

import com.story.platform.api.config.auth.AuthContext
import com.story.platform.api.config.auth.RequestAuthContext
import com.story.platform.core.common.model.dto.ApiResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ReactionUpsertApi(
    private val reactionUpsertHandler: ReactionUpsertHandler,
) {

    @PutMapping("/v1/resources/reactions/components/{componentId}/spaces/{spaceIds}")
    suspend fun upsertReaction(
        @PathVariable componentId: String,
        @PathVariable spaceIds: String,
        @Valid @RequestBody request: ReactionUpsertApiRequest,
        @RequestAuthContext authContext: AuthContext,
    ): ApiResponse<Nothing?> {
        reactionUpsertHandler.upsertReaction(
            workspaceId = authContext.workspaceId,
            componentId = componentId,
            spaceIds = spaceIds,
            request = request,
        )
        return ApiResponse.OK
    }

}
