package com.story.worker.application.feed

import com.story.core.common.annotation.HandlerAdapter
import com.story.core.common.model.CursorDirection
import com.story.core.common.model.dto.CursorRequest
import com.story.core.domain.event.EventAction
import com.story.core.domain.event.EventRecord
import com.story.core.domain.feed.FeedEvent
import com.story.core.domain.feed.FeedModifier
import com.story.core.domain.feed.FeedSubscriberRetriever
import kotlinx.coroutines.coroutineScope

@HandlerAdapter
class ModifyFeedFanoutHandler(
    private val feedSubscriberRetriever: FeedSubscriberRetriever,
    private val feedModifier: FeedModifier,
) : FeedFanoutHandler {

    override fun targetEventAction(): EventAction = EventAction.UPDATED

    override suspend fun handle(event: EventRecord<*>, payload: FeedEvent) = coroutineScope {
        var cursor: String? = null
        do {
            val feedSubscribers = feedSubscriberRetriever.listFeedSubscribersBySlot(
                workspaceId = payload.workspaceId,
                feedComponentId = payload.feedComponentId,
                eventKey = event.eventKey,
                slotId = payload.slotId,
                cursorRequest = CursorRequest(cursor = cursor, direction = CursorDirection.NEXT, pageSize = 500),
            )

            feedModifier.modify(
                event = event,
                payload = payload,
                subscriberIds = feedSubscribers.data.map { subscriber -> subscriber.subscriberId },
            )

            cursor = feedSubscribers.cursor.nextCursor
        } while (feedSubscribers.hasNext)
    }

}
