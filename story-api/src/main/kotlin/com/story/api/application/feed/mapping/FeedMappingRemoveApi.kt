package com.story.api.application.feed.mapping

import com.story.api.config.apikey.ApiKeyContext
import com.story.api.config.apikey.RequestApiKey
import com.story.core.common.model.dto.ApiResponse
import com.story.core.domain.resource.ResourceId
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class FeedMappingRemoveApi(
    private val feedMappingRemoveHandler: FeedMappingRemoveHandler,
) {

    @DeleteMapping("/v1/resources/feeds/components/{feedComponentId}/mappings/{sourceResourceId}/{sourceComponentId}/to/subscriptions/{subscriptionComponentId}")
    suspend fun remove(
        @PathVariable feedComponentId: String,
        @PathVariable sourceResourceId: String,
        @PathVariable sourceComponentId: String,
        @PathVariable subscriptionComponentId: String,
        @RequestApiKey authContext: ApiKeyContext,
    ): ApiResponse<Nothing?> {
        feedMappingRemoveHandler.remove(
            workspaceId = authContext.workspaceId,
            feedComponentId = feedComponentId,
            sourceResourceId = ResourceId.findByCode(sourceResourceId),
            sourceComponentId = sourceComponentId,
            subscriptionComponentId = subscriptionComponentId,
        )
        return ApiResponse.OK
    }

}
