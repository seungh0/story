package com.story.api.application.feed.mapping

import com.story.core.common.annotation.HandlerAdapter
import com.story.core.domain.component.ComponentRetriever
import com.story.core.domain.feed.mapping.FeedMappingRemoveRequest
import com.story.core.domain.feed.mapping.FeedMappingRemover
import com.story.core.domain.resource.ResourceId

@HandlerAdapter
class FeedMappingRemoveHandler(
    private val componentRetriever: ComponentRetriever,
    private val feedMappingRemover: FeedMappingRemover,
) {

    suspend fun remove(
        workspaceId: String,
        feedComponentId: String,
        sourceResourceId: ResourceId,
        sourceComponentId: String,
        subscriptionComponentId: String,
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

        feedMappingRemover.remove(
            request = FeedMappingRemoveRequest(
                workspaceId = workspaceId,
                feedComponentId = feedComponentId,
                resourceId = sourceResourceId,
                componentId = sourceComponentId,
                subscriptionComponentId = subscriptionComponentId,
            )
        )
    }

}
