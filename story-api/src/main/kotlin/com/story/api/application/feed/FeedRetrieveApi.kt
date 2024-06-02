package com.story.api.application.feed

import com.story.api.config.apikey.ApiKeyContext
import com.story.api.config.apikey.RequestApiKey
import com.story.core.common.model.dto.ApiResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class FeedRetrieveApi(
    private val feedRetrieveHandler: FeedRetrieveHandler,
) {

    @GetMapping("/v1/resources/feeds/components/{componentId}/subscribers/{subscriberId}")
    suspend fun listFeeds(
        @PathVariable componentId: String,
        @PathVariable subscriberId: String,
        @Valid request: FeedListRequest,
        @RequestApiKey authContext: ApiKeyContext,
    ): ApiResponse<FeedListResponse> {
        val response = feedRetrieveHandler.listFeeds(
            workspaceId = authContext.workspaceId,
            feedComponentId = componentId,
            subscriberId = subscriberId,
            request = request,
        )
        return ApiResponse.ok(response)
    }

}
