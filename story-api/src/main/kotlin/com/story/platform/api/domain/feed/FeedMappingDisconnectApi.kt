package com.story.platform.api.domain.feed

import com.story.platform.api.config.auth.AuthContext
import com.story.platform.api.config.auth.RequestAuthContext
import com.story.platform.core.common.model.dto.ApiResponse
import com.story.platform.core.domain.resource.ResourceId
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class FeedMappingDisconnectApi(
    private val feedMappingDisconnectHandler: FeedMappingDisconnectHandler,
) {

    @DeleteMapping("/v1/feeds/{feedComponentId}/connect/{sourceResourceId}/{sourceComponentId}/to/{targetResourceId}/{targetComponentId}")
    suspend fun disconnectFeedMapping(
        @PathVariable feedComponentId: String,
        @PathVariable sourceResourceId: String,
        @PathVariable sourceComponentId: String,
        @PathVariable targetResourceId: String,
        @PathVariable targetComponentId: String,
        @Valid request: FeedMappingDisconnectApiRequest,
        @RequestAuthContext authContext: AuthContext,
    ): ApiResponse<Nothing?> {
        feedMappingDisconnectHandler.disconnect(
            workspaceId = authContext.workspaceId,
            feedComponentId = feedComponentId,
            sourceResourceId = ResourceId.findByCode(sourceResourceId),
            sourceComponentId = sourceComponentId,
            targetResourceId = ResourceId.findByCode(targetResourceId),
            targetComponentId = targetComponentId,
            request = request,
        )
        return ApiResponse.OK
    }

}
