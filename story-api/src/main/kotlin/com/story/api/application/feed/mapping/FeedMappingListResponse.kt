package com.story.api.application.feed.mapping

import com.story.core.domain.feed.mapping.FeedMapping

data class FeedMappingListResponse(
    val feedMappings: List<FeedMappingResponse>,
) {

    companion object {
        fun of(
            feedMappings: List<FeedMapping>,
        ) = FeedMappingListResponse(
            feedMappings = feedMappings.map { feedMapping -> FeedMappingResponse.of(feedMapping = feedMapping) },
        )
    }

}
