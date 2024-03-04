package com.story.worker.application.feed

import com.story.core.domain.event.EventAction

fun interface FeedItemFanoutActionHandlerFinder {

    operator fun get(eventAction: EventAction): FeedItemFanoutActionHandler?

}
