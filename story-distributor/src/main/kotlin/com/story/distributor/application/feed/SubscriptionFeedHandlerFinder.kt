package com.story.distributor.application.feed

import com.story.core.domain.event.EventAction

fun interface SubscriptionFeedHandlerFinder {

    operator fun get(eventAction: EventAction): SubscriptionFeedActionHandler?

}
