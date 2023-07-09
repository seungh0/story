package com.story.platform.api.domain.feed

import com.story.platform.api.config.auth.AuthContext
import com.story.platform.api.config.auth.RequestAuthContext
import com.story.platform.core.common.model.ApiResponse
import com.story.platform.core.domain.feed.FeedMappingConnectRequest
import com.story.platform.core.domain.feed.FeedMappingConnector
import com.story.platform.core.domain.resource.ResourceId
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class FeedMappingConnectApi(
    private val feedMappingConnector: FeedMappingConnector,
) {

    @PostMapping("/v1/feed-mappings/resources/{resourceId}/components/{componentId}/to/subscription-components/{subscriptionComponentId}")
    suspend fun connectFeedMapping(
        @PathVariable resourceId: String,
        @PathVariable componentId: String,
        @PathVariable subscriptionComponentId: String,
        @Valid @RequestBody request: FeedMappingConnectApiRequest,
        @RequestAuthContext authContext: AuthContext,
    ): ApiResponse<Nothing?> {
        feedMappingConnector.connect(
            request = FeedMappingConnectRequest(
                workspaceId = authContext.workspaceId,
                resourceId = ResourceId.findByCode(resourceId),
                componentId = componentId,
                eventAction = request.eventAction,
                description = request.description,
                subscriptionComponentId = subscriptionComponentId,
            )
        )
        return ApiResponse.OK
    }

}
