package com.story.distributor.application.feed

import com.story.core.common.annotation.HandlerAdapter
import com.story.core.domain.event.EventAction
import com.story.core.domain.event.EventRecord
import com.story.core.domain.post.PostEvent

@HandlerAdapter
class PostFeedRemoveActionHandler(
    private val postFeedEventDistributor: PostFeedEventDistributor,
) : PostFeedEventActionHandler {

    override fun eventAction(): EventAction = EventAction.REMOVED

    override suspend fun handle(event: EventRecord<*>, payload: PostEvent) {
        postFeedEventDistributor.distribute(
            payload = payload,
            eventAction = event.eventAction,
        )
    }

}