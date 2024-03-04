package com.story.distributor.application.feed

import com.story.core.domain.event.EventAction
import com.story.core.domain.event.EventRecord
import com.story.core.domain.subscription.SubscriptionEvent

interface SubscriptionFeedActionHandler {

    fun eventAction(): EventAction

    suspend fun handle(event: EventRecord<*>, payload: SubscriptionEvent)

}
