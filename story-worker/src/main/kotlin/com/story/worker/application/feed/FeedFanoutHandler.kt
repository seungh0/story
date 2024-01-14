package com.story.worker.application.feed

import com.story.core.domain.event.EventAction
import com.story.core.domain.event.EventRecord
import com.story.core.domain.feed.FeedFanoutEvent

interface FeedFanoutHandler {

    fun targetEventAction(): EventAction

    suspend fun handle(event: EventRecord<*>, payload: FeedFanoutEvent)

}
