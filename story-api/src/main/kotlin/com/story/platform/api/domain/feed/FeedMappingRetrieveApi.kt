package com.story.platform.api.domain.feed

import com.story.platform.api.config.auth.AuthContext
import com.story.platform.api.config.auth.RequestAuthContext
import com.story.platform.core.common.model.dto.ApiResponse
import com.story.platform.core.domain.resource.ResourceId
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class FeedMappingRetrieveApi(
    private val feedMappingRetrieveHandler: FeedMappingRetrieveHandler,
) {

    @GetMapping("/v1/resources/feeds/{feedComponentId}/mappings/{sourceResourceId}/{sourceComponentId}")
    suspend fun connectFeedMapping(
        @PathVariable feedComponentId: String,
        @PathVariable sourceResourceId: String,
        @PathVariable sourceComponentId: String,
        @RequestAuthContext authContext: AuthContext,
    ): ApiResponse<FeedMappingListApiResponse> {
        val feedMappings = feedMappingRetrieveHandler.listConnectedFeedMappings(
            workspaceId = authContext.workspaceId,
            sourceResourceId = ResourceId.findByCode(sourceResourceId),
            sourceComponentId = sourceComponentId,
        )
        return ApiResponse.ok(feedMappings)
    }

}
