package com.story.platform.executor.domain.feed

import com.story.platform.core.common.model.CursorDirection
import com.story.platform.core.common.model.dto.CursorRequest
import com.story.platform.core.common.spring.HandlerAdapter
import com.story.platform.core.domain.event.EventAction
import com.story.platform.core.domain.event.EventRecord
import com.story.platform.core.domain.feed.FeedCreator
import com.story.platform.core.domain.feed.FeedEvent
import com.story.platform.core.domain.feed.FeedSubscriberRetriever
import kotlinx.coroutines.coroutineScope

@HandlerAdapter
class FeedCreateHandler(
    private val feedSubscriberRetriever: FeedSubscriberRetriever,
    private val feedCreator: FeedCreator,
) : FeedPublisher {

    override fun targetEventAction(): EventAction = EventAction.CREATED

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

            feedCreator.createFeeds(
                event = event,
                payload = payload,
                subscriberIds = feedSubscribers.data.map { subscriber -> subscriber.subscriberId }
            )
            cursor = feedSubscribers.cursor.nextCursor
        } while (feedSubscribers.hasNext)
    }

}
