package com.story.platform.api.domain.feed

import com.story.platform.core.common.error.NotSupportedException
import com.story.platform.core.domain.component.ComponentRetriever
import com.story.platform.core.domain.feed.configuration.FeedMappingDisconnectRequest
import com.story.platform.core.domain.feed.configuration.FeedMappingDisconnector
import com.story.platform.core.domain.resource.ResourceId
import org.springframework.stereotype.Service

@Service
class FeedMappingDisconnectHandler(
    private val componentRetriever: ComponentRetriever,
    private val feedMappingDisconnector: FeedMappingDisconnector,
) {

    suspend fun disconnect(
        workspaceId: String,
        feedComponentId: String,
        sourceResourceId: ResourceId,
        sourceComponentId: String,
        targetResourceId: ResourceId,
        targetComponentId: String,
        request: FeedMappingDisconnectApiRequest,
    ) {
        if (ResourceId.SUBSCRIPTIONS != targetResourceId) {
            throw NotSupportedException("현재 지원하지 않는 Feed Target Resource($targetResourceId) 입니다.")
        }

        setOf(
            ResourceId.FEEDS to feedComponentId,
            sourceResourceId to sourceComponentId,
            targetResourceId to targetComponentId,
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
                eventAction = request.eventAction,
                targetResourceId = targetResourceId,
                targetComponentId = targetComponentId,
            )
        )
    }

}
