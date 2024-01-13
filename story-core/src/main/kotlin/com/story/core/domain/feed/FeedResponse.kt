package com.story.core.domain.feed

import com.story.core.domain.resource.ResourceId

data class FeedResponse(
    val feedId: Long,
    val eventKey: String,
    val sourceResourceId: ResourceId,
    val sourceComponentId: String,
) {

    companion object {
        fun of(feed: Feed): FeedResponse {
            return FeedResponse(
                feedId = feed.key.feedId,
                sourceResourceId = feed.sourceResourceId,
                sourceComponentId = feed.sourceComponentId,
                eventKey = feed.eventKey,
            )
        }
    }

}
