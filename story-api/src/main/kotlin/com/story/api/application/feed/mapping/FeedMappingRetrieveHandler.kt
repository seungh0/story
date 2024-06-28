package com.story.api.application.feed.mapping

import com.story.api.application.component.ComponentCheckHandler
import com.story.core.common.annotation.HandlerAdapter
import com.story.core.domain.feed.mapping.FeedMappingReaderWithCache
import com.story.core.domain.resource.ResourceId

@HandlerAdapter
class FeedMappingRetrieveHandler(
    private val componentCheckHandler: ComponentCheckHandler,
    private val feedMappingReaderWithCache: FeedMappingReaderWithCache,
) {

    suspend fun listConnectedFeedMappings(
        workspaceId: String,
        sourceResourceId: ResourceId,
        sourceComponentId: String,
    ): FeedMappingListResponse {
        componentCheckHandler.checkExistsComponent(
            workspaceId = workspaceId,
            resourceId = sourceResourceId,
            componentId = sourceComponentId,
        )
        val feedMappings = feedMappingReaderWithCache.listConnectedFeedMappings(
            workspaceId = workspaceId,
            sourceResourceId = sourceResourceId,
            sourceComponentId = sourceComponentId,
        )

        return FeedMappingListResponse.of(feedMappings = feedMappings)
    }

}
