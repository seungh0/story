package com.story.core.domain.feed

data class FeedResponse(
    val feedId: Long,
    val resourceId: String,
    val componentId: String,
) {

    companion object {
        fun of(feed: Feed): FeedResponse {
            return FeedResponse(
                feedId = feed.key.feedId,
                resourceId = feed.sourceResourceId.code,
                componentId = feed.sourceComponentId,
            )
        }
    }

}
