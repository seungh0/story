package com.story.worker.application.feed

import com.story.core.common.annotation.HandlerAdapter
import com.story.core.domain.event.EventAction
import com.story.core.domain.event.EventRecord
import com.story.core.domain.feed.FeedCreator
import com.story.core.domain.feed.FeedEvent
import com.story.core.domain.subscription.SubscriberRepository
import kotlinx.coroutines.coroutineScope
import org.springframework.data.cassandra.core.query.CassandraPageRequest
import org.springframework.data.domain.Pageable

@HandlerAdapter
class FeedFanoutCreateHandler(
    private val feedCreator: FeedCreator,
    private val subscriberRepository: SubscriberRepository,
) : FeedFanoutHandler {

    override fun targetEventAction(): EventAction = EventAction.CREATED

    override suspend fun handle(event: EventRecord<*>, payload: FeedEvent) = coroutineScope {
        var pageable: Pageable = CassandraPageRequest.first(500)
        do {
            val subscribers = subscriberRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyTargetIdAndKeySlotIdOrderByKeySubscriberIdAsc(
                workspaceId = payload.workspaceId,
                componentId = payload.subscriptionComponentId,
                targetId = payload.targetId,
                slotId = payload.slotId,
                pageable = pageable
            )

            feedCreator.createFeeds(
                event = event,
                payload = payload,
                subscriberIds = subscribers.content.map { subscriber -> subscriber.key.subscriberId },
            )

            pageable = subscribers.nextPageable()
        } while (subscribers.hasNext())
    }

}
