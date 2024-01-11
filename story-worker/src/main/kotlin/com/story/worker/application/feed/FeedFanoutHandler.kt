package com.story.worker.application.feed

import com.story.core.domain.event.EventAction
import com.story.core.domain.event.EventRecord
import com.story.core.domain.feed.FeedEvent

interface FeedFanoutHandler {

    fun targetEventAction(): EventAction

    suspend fun handle(event: EventRecord<*>, payload: FeedEvent)

}
