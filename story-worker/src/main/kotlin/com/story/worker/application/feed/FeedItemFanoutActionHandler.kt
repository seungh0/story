package com.story.worker.application.feed

import com.story.core.domain.event.EventAction
import com.story.core.domain.event.EventRecord
import com.story.core.domain.feed.FeedFanoutMessage

interface FeedItemFanoutActionHandler {

    fun eventAction(): EventAction

    suspend fun handle(record: EventRecord<*>, payload: FeedFanoutMessage)

}
