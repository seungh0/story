package com.story.platform.api.domain.feed

import com.story.platform.api.config.auth.AuthContext
import com.story.platform.api.config.auth.RequestAuthContext
import com.story.platform.core.common.model.dto.ApiResponse
import com.story.platform.core.domain.resource.ResourceId
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class FeedMappingConnectApi(
    private val feedMappingConnectHandler: FeedMappingConnectHandler,
) {

    @PostMapping("/v1/feeds/{feedComponentId}/connect/{sourceResourceId}/{sourceComponentId}/to/subscriptions/{subscriptionComponentId}")
    suspend fun connectFeedMapping(
        @PathVariable feedComponentId: String,
        @PathVariable sourceResourceId: String,
        @PathVariable sourceComponentId: String,
        @PathVariable subscriptionComponentId: String,
        @Valid @RequestBody request: FeedMappingConnectApiRequest,
        @RequestAuthContext authContext: AuthContext,
    ): ApiResponse<Nothing?> {
        feedMappingConnectHandler.connect(
            workspaceId = authContext.workspaceId,
            feedComponentId = feedComponentId,
            sourceResourceId = ResourceId.findByCode(sourceResourceId),
            sourceComponentId = sourceComponentId,
            subscriptionComponentId = subscriptionComponentId,
            request = request,
        )
        return ApiResponse.OK
    }

}
