package com.story.worker.application.feed

import com.story.core.common.annotation.HandlerAdapter
import com.story.core.domain.event.EventAction
import com.story.core.domain.event.EventRecord
import com.story.core.domain.feed.FeedFanoutMessage
import com.story.core.domain.feed.FeedRemover
import com.story.core.domain.subscription.SubscriberRepository
import kotlinx.coroutines.coroutineScope
import org.springframework.data.cassandra.core.query.CassandraPageRequest
import org.springframework.data.domain.Pageable

@HandlerAdapter
class FeedItemFanoutRemoveActionHandler(
    private val feedRemover: FeedRemover,
    private val subscriberRepository: SubscriberRepository,
) : FeedItemFanoutActionHandler {

    override fun eventAction(): EventAction = EventAction.REMOVED

    override suspend fun handle(record: EventRecord<*>, payload: FeedFanoutMessage) = coroutineScope {
        var pageable: Pageable = CassandraPageRequest.first(500)
        do {
            val subscribers = subscriberRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyTargetIdAndKeySlotIdOrderByKeySubscriberIdAsc(
                workspaceId = payload.workspaceId,
                componentId = payload.subscriptionComponentId,
                targetId = payload.targetId,
                slotId = payload.slotId,
                pageable = pageable
            )

            feedRemover.remove(
                workspaceId = payload.workspaceId,
                componentId = payload.componentId,
                ownerIds = subscribers.content.map { subscriber -> subscriber.key.subscriberId },
                item = payload.item,
                options = payload.options,
            )

            pageable = subscribers.nextPageable()
        } while (subscribers.hasNext())
    }

}
