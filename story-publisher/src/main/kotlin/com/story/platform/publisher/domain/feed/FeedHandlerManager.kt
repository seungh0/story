package com.story.platform.publisher.domain.feed

import com.story.platform.core.domain.event.EventRecord
import com.story.platform.core.domain.feed.FeedEvent
import org.springframework.stereotype.Service

@Service
class FeedHandlerManager(
    private val feedHandlerFinder: FeedHandlerFinder,
) {

    suspend fun handle(event: EventRecord<*>, payload: FeedEvent) {
        val publisher = feedHandlerFinder.get(eventAction = event.eventAction)
        publisher.handle(event = event, payload = payload)
    }

}
