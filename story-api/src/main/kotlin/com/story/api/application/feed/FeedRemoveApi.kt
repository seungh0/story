package com.story.api.application.feed

import com.story.api.config.apikey.ApiKeyContext
import com.story.api.config.apikey.RequestApiKey
import com.story.core.common.model.dto.ApiResponse
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class FeedRemoveApi(
    private val feedRemoveHandler: FeedRemoveHandler,
) {

    @DeleteMapping("/v1/feed-components/{componentId}/owners/{ownerId}/feeds/{feedId}")
    suspend fun removeFeed(
        @PathVariable componentId: String,
        @PathVariable ownerId: String,
        @PathVariable feedId: String,
        @RequestApiKey authContext: ApiKeyContext,
    ): ApiResponse<Nothing?> {
        feedRemoveHandler.remove(
            workspaceId = authContext.workspaceId,
            componentId = componentId,
            ownerId = ownerId,
            feedId = feedId,
        )
        return ApiResponse.OK
    }

}
