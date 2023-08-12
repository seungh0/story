package com.story.platform.api.domain.feed

import com.story.platform.api.domain.component.ComponentCheckHandler
import com.story.platform.core.common.model.Cursor
import com.story.platform.core.common.model.CursorResult
import com.story.platform.core.common.spring.HandlerAdapter
import com.story.platform.core.domain.feed.mapping.FeedMappingRetriever
import com.story.platform.core.domain.resource.ResourceId

@HandlerAdapter
class FeedMappingRetrieveHandler(
    private val componentCheckHandler: ComponentCheckHandler,
    private val feedMappingRetriever: FeedMappingRetriever,
) {

    suspend fun listConnectedFeedMappings(
        workspaceId: String,
        sourceResourceId: ResourceId,
        sourceComponentId: String,
    ): CursorResult<FeedMappingApiResponse, String> {
        componentCheckHandler.checkExistsComponent(
            workspaceId = workspaceId,
            resourceId = sourceResourceId,
            componentId = sourceComponentId,
        )
        val feedMappings = feedMappingRetriever.listConnectedFeedMappings(
            workspaceId = workspaceId,
            sourceResourceId = sourceResourceId,
            sourceComponentId = sourceComponentId,
        ).map { feedMapping -> FeedMappingApiResponse.of(feedMapping = feedMapping) }

        return CursorResult.of(
            data = feedMappings,
            cursor = Cursor.noMore(),
        )
    }

}
