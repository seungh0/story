package com.story.platform.api.domain.feed

import com.story.platform.core.common.spring.HandlerAdapter
import com.story.platform.core.domain.component.ComponentRetriever
import com.story.platform.core.domain.feed.mapping.FeedMappingDisconnectRequest
import com.story.platform.core.domain.feed.mapping.FeedMappingDisconnector
import com.story.platform.core.domain.resource.ResourceId

@HandlerAdapter
class FeedMappingDisconnectHandler(
    private val componentRetriever: ComponentRetriever,
    private val feedMappingDisconnector: FeedMappingDisconnector,
) {

    suspend fun disconnect(
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

        feedMappingDisconnector.disconnect(
            request = FeedMappingDisconnectRequest(
                workspaceId = workspaceId,
                feedComponentId = feedComponentId,
                resourceId = sourceResourceId,
                componentId = sourceComponentId,
                subscriptionComponentId = subscriptionComponentId,
            )
        )
    }

}
