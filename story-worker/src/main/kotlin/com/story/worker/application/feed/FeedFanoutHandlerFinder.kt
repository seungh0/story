package com.story.worker.application.feed

import com.story.core.domain.event.EventAction

fun interface FeedFanoutHandlerFinder {

    operator fun get(eventAction: EventAction): FeedFanoutHandler

}
