package com.story.platform.publisher.application.feed

import com.story.platform.core.common.annotation.HandlerAdapter
import com.story.platform.core.domain.event.EventAction
import com.story.platform.core.domain.event.EventRecord
import com.story.platform.core.domain.feed.FeedCreator
import com.story.platform.core.domain.feed.FeedEvent
import com.story.platform.core.domain.subscription.SubscriberRepository
import kotlinx.coroutines.coroutineScope
import org.springframework.data.cassandra.core.query.CassandraPageRequest
import org.springframework.data.domain.Pageable

@HandlerAdapter
class FeedCreateHandler(
    private val feedCreator: FeedCreator,
    private val subscriberRepository: SubscriberRepository,
) : FeedHandler {

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
