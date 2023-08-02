package com.story.platform.core.domain.feed

import com.story.platform.core.common.json.toObject
import com.story.platform.core.domain.event.BaseEvent
import com.story.platform.core.domain.event.EventAction
import com.story.platform.core.domain.resource.ResourceId

data class FeedResponse<T : BaseEvent>(
    val resourceId: ResourceId,
    val componentId: String,
    val eventAction: EventAction,
    val payload: T,
) {

    companion object {
        fun <T : BaseEvent> of(feed: Feed): FeedResponse<T> {
            val payload = feed.payloadJson.toObject(feed.sourceResourceId.feedPayloadClazz!!) as T
            return FeedResponse(
                resourceId = feed.sourceResourceId,
                componentId = feed.sourceComponentId,
                eventAction = feed.eventAction,
                payload = payload,
            )
        }
    }

}
