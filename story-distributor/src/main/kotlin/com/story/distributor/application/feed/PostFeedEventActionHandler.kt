package com.story.distributor.application.feed

import com.story.core.domain.event.EventAction
import com.story.core.domain.event.EventRecord
import com.story.core.domain.post.PostEvent

interface PostFeedEventActionHandler {

    fun eventAction(): EventAction

    suspend fun handle(event: EventRecord<*>, payload: PostEvent)

}
