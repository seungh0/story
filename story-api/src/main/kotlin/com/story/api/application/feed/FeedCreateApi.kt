package com.story.api.application.feed

import com.story.api.config.apikey.ApiKeyContext
import com.story.api.config.apikey.RequestApiKey
import com.story.core.common.model.dto.ApiResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class FeedCreateApi(
    private val feedCreateHandler: FeedCreateHandler,
) {

    @PostMapping("/v1/feed-components/{componentId}/owners/{ownerId}/feeds")
    suspend fun createFeed(
        @PathVariable componentId: String,
        @PathVariable ownerId: String,
        @Valid @RequestBody request: FeedListCreateRequest,
        @RequestApiKey authContext: ApiKeyContext,
    ): ApiResponse<Nothing?> {
        feedCreateHandler.createFeeds(
            workspaceId = authContext.workspaceId,
            componentId = componentId,
            ownerId = ownerId,
            request = request,
        )
        return ApiResponse.OK
    }

}
