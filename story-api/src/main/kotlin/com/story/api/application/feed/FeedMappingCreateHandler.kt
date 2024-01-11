package com.story.api.application.feed

import com.story.core.common.annotation.HandlerAdapter
import com.story.core.domain.component.ComponentRetriever
import com.story.core.domain.feed.mapping.FeedMappingCreateRequest
import com.story.core.domain.feed.mapping.FeedMappingCreator
import com.story.core.domain.resource.ResourceId

@HandlerAdapter
class FeedMappingCreateHandler(
    private val componentRetriever: ComponentRetriever,
    private val feedMappingCreator: FeedMappingCreator,
) {

    suspend fun create(
        workspaceId: String,
        feedComponentId: String,
        sourceResourceId: ResourceId,
        sourceComponentId: String,
        subscriptionComponentId: String,
        request: FeedMappingCreateApiRequest,
    ) {
        setOf(
            ResourceId.FEEDS to feedComponentId,
            sourceResourceId to sourceComponentId,
            ResourceId.SUBSCRIPTIONS to subscriptionComponentId,
        ).forEach { (resourceId: ResourceId, componentId: String) ->
            componentRetriever.getComponent(
                workspaceId = workspaceId,
                resourceId = resourceId,
                componentId = componentId,
            )
        }

        feedMappingCreator.create(
            request = FeedMappingCreateRequest(
                workspaceId = workspaceId,
                feedComponentId = feedComponentId,
                sourceResourceId = sourceResourceId,
                sourceComponentId = sourceComponentId,
                description = request.description,
                subscriptionComponentId = subscriptionComponentId,
            )
        )
    }

}
