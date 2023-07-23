package com.story.platform.api.domain.feed

import com.story.platform.api.config.auth.AuthContext
import com.story.platform.api.config.auth.RequestAuthContext
import com.story.platform.core.common.error.NotSupportedException
import com.story.platform.core.common.model.dto.ApiResponse
import com.story.platform.core.domain.component.ComponentRetriever
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
    private val componentRetriever: ComponentRetriever,
) {

    @PostMapping("/v1/feeds/{feedComponentId}/connect/{sourceResourceId}/{sourceComponentId}/to/{targetResourceId}/{targetComponentId}")
    suspend fun connectFeedMapping(
        @PathVariable feedComponentId: String,
        @PathVariable sourceResourceId: String,
        @PathVariable sourceComponentId: String,
        @PathVariable targetResourceId: String,
        @PathVariable targetComponentId: String,
        @Valid @RequestBody request: FeedMappingConnectApiRequest,
        @RequestAuthContext authContext: AuthContext,
    ): ApiResponse<Nothing?> {
        val targetResource = ResourceId.findByCode(targetResourceId)
        val sourceResource = ResourceId.findByCode(sourceResourceId)

        if (ResourceId.SUBSCRIPTIONS != targetResource) {
            throw NotSupportedException("현재 지원하지 않는 Feed Target Resource($targetResourceId) 입니다.")
        }

        setOf(
            ResourceId.FEEDS to feedComponentId,
            sourceResource to sourceComponentId,
            targetResource to targetComponentId,
        ).forEach { (resourceId: ResourceId, componentId: String) ->
            componentRetriever.getComponent(
                workspaceId = authContext.workspaceId,
                resourceId = resourceId,
                componentId = componentId,
            )
        }

        feedMappingConnector.connect(
            request = FeedMappingConnectRequest(
                workspaceId = authContext.workspaceId,
                feedComponentId = feedComponentId,
                resourceId = sourceResource,
                componentId = sourceComponentId,
                eventAction = request.eventAction,
                description = request.description,
                targetResourceId = targetResource,
                targetComponentId = targetComponentId,
            )
        )
        return ApiResponse.OK
    }

}
