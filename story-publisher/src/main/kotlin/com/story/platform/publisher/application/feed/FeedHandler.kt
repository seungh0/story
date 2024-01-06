package com.story.platform.publisher.application.feed

import com.story.platform.core.domain.event.EventAction
import com.story.platform.core.domain.event.EventRecord
import com.story.platform.core.domain.feed.FeedEvent

interface FeedHandler {

    fun targetEventAction(): EventAction

    suspend fun handle(event: EventRecord<*>, payload: FeedEvent)

}
