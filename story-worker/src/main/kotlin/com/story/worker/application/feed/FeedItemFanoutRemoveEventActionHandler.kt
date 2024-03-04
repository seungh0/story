package com.story.worker.application.feed

import com.story.core.common.annotation.HandlerAdapter
import com.story.core.common.model.CursorDirection
import com.story.core.common.model.dto.CursorRequest
import com.story.core.domain.event.EventAction
import com.story.core.domain.event.EventRecord
import com.story.core.domain.feed.FeedDistributedEvent
import com.story.core.domain.feed.FeedRemover
import com.story.core.domain.feed.FeedSubscriberRetriever
import kotlinx.coroutines.coroutineScope

@HandlerAdapter
class FeedItemFanoutRemoveEventActionHandler(
    private val feedSubscriberRetriever: FeedSubscriberRetriever,
    private val feedRemover: FeedRemover,
) : FeedItemFanoutActionHandler {

    override fun eventAction(): EventAction = EventAction.REMOVED

    override suspend fun handle(event: EventRecord<*>, payload: FeedDistributedEvent) = coroutineScope {
        var cursor: String? = null
        do {
            val feedSubscribers = feedSubscriberRetriever.listFeedSubscribersBySlot(
                workspaceId = payload.workspaceId,
                feedComponentId = payload.feedComponentId,
                eventKey = event.eventKey,
                slotId = payload.slotId,
                cursorRequest = CursorRequest(cursor = cursor, direction = CursorDirection.NEXT, pageSize = 500),
            )

            feedRemover.remove(feedSubscribers = feedSubscribers.data)

            cursor = feedSubscribers.cursor.nextCursor
        } while (feedSubscribers.hasNext)
    }

}
