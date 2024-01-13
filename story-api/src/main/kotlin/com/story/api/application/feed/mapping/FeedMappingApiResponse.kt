package com.story.api.application.feed.mapping

import com.story.core.domain.feed.mapping.FeedMappingResponse
import com.story.core.domain.resource.ResourceId

data class FeedMappingApiResponse(
    val resourceId: String,
    val componentId: String,
) {

    companion object {
        fun of(feedMapping: FeedMappingResponse) = FeedMappingApiResponse(
            resourceId = ResourceId.SUBSCRIPTIONS.code,
            componentId = feedMapping.subscriptionComponentId,
        )
    }

}
