package com.story.platform.api.domain.feed

import com.story.platform.api.config.auth.AuthContext
import com.story.platform.api.config.auth.RequestAuthContext
import com.story.platform.core.common.model.CursorResult
import com.story.platform.core.common.model.dto.ApiResponse
import com.story.platform.core.common.model.dto.CursorRequest
import com.story.platform.core.domain.event.BaseEvent
import com.story.platform.core.domain.feed.FeedResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class FeedRetrieveApi(
    private val feedRetrieveHandler: FeedRetrieveHandler,
) {

    @GetMapping("/v1/feeds/components/{componentId}/subscriber/{subscriberId}")
    suspend fun listFeeds(
        @PathVariable componentId: String,
        @PathVariable subscriberId: String,
        @Valid cursorRequest: CursorRequest,
        @RequestAuthContext authContext: AuthContext,
    ): ApiResponse<CursorResult<FeedResponse<out BaseEvent>, String>> {
        val response = feedRetrieveHandler.listFeeds(
            workspaceId = authContext.workspaceId,
            feedComponentId = componentId,
            subscriberId = subscriberId,
            cursorRequest = cursorRequest,
        )
        return ApiResponse.ok(response)
    }

}
