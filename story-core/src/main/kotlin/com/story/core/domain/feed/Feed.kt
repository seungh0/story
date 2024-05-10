package com.story.core.domain.feed

import com.story.core.domain.resource.ResourceId

data class Feed(
    val feedId: Long,
    val subscriberId: String,
    val eventKey: String,
    val sourceResourceId: ResourceId,
    val sourceComponentId: String,
) {

    companion object {
        fun of(feed: FeedEntity): Feed {
            return Feed(
                feedId = feed.key.feedId,
                sourceResourceId = feed.sourceResourceId,
                sourceComponentId = feed.sourceComponentId,
                eventKey = feed.eventKey,
                subscriberId = feed.key.subscriberId,
            )
        }

        fun of(feed: FeedSubscriberEntity): Feed {
            return Feed(
                feedId = feed.feedId,
                sourceResourceId = feed.sourceResourceId,
                sourceComponentId = feed.sourceComponentId,
                eventKey = feed.key.eventKey,
                subscriberId = feed.key.subscriberId,
            )
        }
    }

}
