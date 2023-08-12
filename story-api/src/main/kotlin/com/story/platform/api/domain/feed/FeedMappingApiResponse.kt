package com.story.platform.api.domain.feed

import com.story.platform.core.domain.feed.mapping.FeedMappingResponse
import com.story.platform.core.domain.resource.ResourceId

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
