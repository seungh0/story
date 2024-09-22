package com.story.worker.application.feed

import com.story.core.common.annotation.HandlerAdapter
import com.story.core.common.model.CursorDirection
import com.story.core.common.model.dto.CursorRequest
import com.story.core.domain.component.ComponentReader
import com.story.core.domain.feed.FeedRemover
import com.story.core.domain.resource.ResourceId

@HandlerAdapter
class FeedChannelClearHandler(
    private val feedRemover: FeedRemover,
    private val componentReader: ComponentReader,
) {

    suspend fun clearAllFeedByChannelId(
        workspaceId: String,
        ownerId: String,
        channelId: String,
    ) {
        var cursor = CursorRequest.first(direction = CursorDirection.NEXT, pageSize = 100)
        do {
            val components = componentReader.listComponents(
                workspaceId = workspaceId, resourceId = ResourceId.FEEDS, cursorRequest = cursor
            )

            for (component in components.data) {
                feedRemover.clearByChannel(
                    workspaceId = workspaceId,
                    componentId = component.componentId,
                    ownerId = ownerId,
                    channelId = channelId,
                )
            }

            if (components.hasNext()) {
                cursor = cursor.copy(cursor = components.cursor.nextCursor)
            }
        } while (components.hasNext())
    }

}
