package com.story.platform.api.domain.feed

import com.story.platform.core.domain.feed.mapping.FeedMappingResponse

data class FeedMappingListApiResponse(
    val feedMappings: List<FeedMappingApiResponse>,
) {

    companion object {
        fun of(
            feedMappings: List<FeedMappingResponse>,
        ) = FeedMappingListApiResponse(
            feedMappings = feedMappings.map { feedMapping -> FeedMappingApiResponse.of(feedMapping = feedMapping) },
        )
    }

}
