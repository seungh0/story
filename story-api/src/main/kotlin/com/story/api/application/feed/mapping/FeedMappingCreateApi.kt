package com.story.api.application.feed.mapping

import com.story.api.config.apikey.ApiKeyContext
import com.story.api.config.apikey.RequestApiKey
import com.story.core.common.model.dto.ApiResponse
import com.story.core.domain.resource.ResourceId
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class FeedMappingCreateApi(
    private val feedMappingCreateHandler: FeedMappingCreateHandler,
) {

    @PostMapping("/v1/resources/feeds/components/{feedComponentId}/mappings/{sourceResourceId}/{sourceComponentId}/to/subscriptions/{subscriptionComponentId}")
    suspend fun create(
        @PathVariable feedComponentId: String,
        @PathVariable sourceResourceId: String,
        @PathVariable sourceComponentId: String,
        @PathVariable subscriptionComponentId: String,
        @Valid @RequestBody request: FeedMappingCreateApiRequest,
        @RequestApiKey authContext: ApiKeyContext,
    ): ApiResponse<Nothing?> {
        feedMappingCreateHandler.create(
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
