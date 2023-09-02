package com.story.platform.api.domain.reaction

import com.story.platform.api.config.auth.AuthContext
import com.story.platform.api.config.auth.RequestAuthContext
import com.story.platform.core.common.model.dto.ApiResponse
import com.story.platform.core.domain.reaction.ReactionCreator
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ReactionCreateApi(
    private val reactionCreator: ReactionCreator,
) {

    @PutMapping("/v1/reactions/components/{componentId}/targets/{targetId}")
    suspend fun upsertReaction(
        @PathVariable componentId: String,
        @PathVariable targetId: String,
        @Valid @RequestBody request: ReactionCreateApiRequest,
        @RequestAuthContext authContext: AuthContext,
    ): ApiResponse<Nothing?> {
        reactionCreator.upsert(
            workspaceId = authContext.workspaceId,
            componentId = componentId,
            targetId = targetId,
            accountId = request.accountId,
            optionIds = request.options.map { option -> option.optionId }.toSet(),
        )
        return ApiResponse.OK
    }

}
