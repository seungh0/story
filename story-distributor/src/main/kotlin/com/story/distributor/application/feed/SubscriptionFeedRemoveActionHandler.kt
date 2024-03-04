package com.story.distributor.application.feed

import com.story.core.common.annotation.HandlerAdapter
import com.story.core.domain.event.EventAction
import com.story.core.domain.event.EventRecord
import com.story.core.domain.subscription.SubscriptionEvent

@HandlerAdapter
class SubscriptionFeedRemoveActionHandler(
    private val subscriptionFeedEventDistributor: SubscriptionFeedEventDistributor,
) : SubscriptionFeedActionHandler {

    override fun eventAction(): EventAction = EventAction.REMOVED

    override suspend fun handle(event: EventRecord<*>, payload: SubscriptionEvent) {
        subscriptionFeedEventDistributor.distribute(
            payload = payload,
            eventId = event.eventId,
            eventAction = event.eventAction,
            eventKey = event.eventKey,
        )
    }

}
