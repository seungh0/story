package com.story.platform.core.domain.feed

import com.story.platform.core.common.json.toObject
import com.story.platform.core.domain.event.BaseEvent

data class FeedResponse<T : BaseEvent>(
    val resourceId: String,
    val componentId: String,
    val payload: T,
) {

    companion object {
        fun <T : BaseEvent> of(feed: Feed): FeedResponse<T> {
            val payload = feed.payloadJson.toObject(feed.sourceResourceId.feedPayloadClazz!!) as T
            return FeedResponse(
                resourceId = feed.sourceResourceId.code,
                componentId = feed.sourceComponentId,
                payload = payload,
            )
        }
    }

}
