package com.story.platform.api.domain.feed

import com.story.platform.core.common.spring.HandlerAdapter
import com.story.platform.core.domain.component.ComponentRetriever
import com.story.platform.core.domain.feed.mapping.FeedMappingConnectRequest
import com.story.platform.core.domain.feed.mapping.FeedMappingConnector
import com.story.platform.core.domain.resource.ResourceId

@HandlerAdapter
class FeedMappingConnectHandler(
    private val componentRetriever: ComponentRetriever,
    private val feedMappingConnector: FeedMappingConnector,
) {

    suspend fun connect(
        workspaceId: String,
        feedComponentId: String,
        sourceResourceId: ResourceId,
        sourceComponentId: String,
        subscriptionComponentId: String,
        request: FeedMappingConnectApiRequest,
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

        feedMappingConnector.connect(
            request = FeedMappingConnectRequest(
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
