package com.story.core.domain.feed

data class FeedSubscriberResponse(
    val workspaceId: String,
    val feedComponentId: String,
    val eventKey: String,
    val slotId: Long,
    val subscriberId: String,
    val eventId: Long,
) {

    companion object {
        fun of(
            feedSubscriber: FeedSubscriber,
        ) = FeedSubscriberResponse(
            workspaceId = feedSubscriber.key.workspaceId,
            feedComponentId = feedSubscriber.key.feedComponentId,
            eventKey = feedSubscriber.key.eventKey,
            slotId = feedSubscriber.key.slotId,
            subscriberId = feedSubscriber.key.subscriberId,
            eventId = feedSubscriber.feedId,
        )
    }

}
