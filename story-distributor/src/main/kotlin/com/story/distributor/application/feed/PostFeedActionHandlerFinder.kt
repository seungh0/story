package com.story.distributor.application.feed

import com.story.core.domain.event.EventAction

fun interface PostFeedActionHandlerFinder {

    operator fun get(eventAction: EventAction): PostFeedEventActionHandler?

}
