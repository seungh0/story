package com.story.worker.application.feed

import com.story.core.common.annotation.HandlerAdapter
import com.story.core.common.model.CursorDirection
import com.story.core.common.model.dto.CursorRequest
import com.story.core.domain.event.EventAction
import com.story.core.domain.event.EventRecord
import com.story.core.domain.feed.FeedFanoutMessage
import com.story.core.domain.feed.FeedRemover
import com.story.core.domain.feed.FeedSubscriberRetriever
import kotlinx.coroutines.coroutineScope

@HandlerAdapter
class FeedItemFanoutRemoveActionHandler(
    private val feedSubscriberRetriever: FeedSubscriberRetriever,
    private val feedRemover: FeedRemover,
) : FeedItemFanoutActionHandler {

    override fun eventAction(): EventAction = EventAction.REMOVED

    override suspend fun handle(record: EventRecord<*>, payload: FeedFanoutMessage) = coroutineScope {
        var cursor: String? = null
        do {
            val feedSubscribers = feedSubscriberRetriever.listFeedSubscribersBySlot(
                workspaceId = payload.workspaceId,
                feedComponentId = payload.feedComponentId,
                eventKey = record.eventKey,
                slotId = payload.slotId,
                cursorRequest = CursorRequest(cursor = cursor, direction = CursorDirection.NEXT, pageSize = 500),
            )

            feedRemover.remove(
                workspaceId = payload.workspaceId,
                componentId = payload.feedComponentId,
                feedSubscribers = feedSubscribers.data
            )

            cursor = feedSubscribers.cursor.nextCursor
        } while (feedSubscribers.hasNext)
    }

}
