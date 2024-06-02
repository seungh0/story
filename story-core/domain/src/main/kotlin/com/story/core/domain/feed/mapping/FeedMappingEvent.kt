package com.story.core.domain.feed.mapping

import com.story.core.domain.event.BaseEvent
import com.story.core.domain.event.EventAction
import com.story.core.domain.event.EventRecord
import com.story.core.domain.resource.ResourceId

data class FeedMappingEvent(
    val workspaceId: String,
    val feedComponentId: String,
    val sourceResourceId: ResourceId,
    val sourceComponentId: String,
    val subscriptionComponentId: String,
) : BaseEvent {

    companion object {
        fun created(
            workspaceId: String,
            feedComponentId: String,
            sourceResourceId: ResourceId,
            sourceComponentId: String,
            subscriptionComponentId: String,
        ) = EventRecord(
            eventAction = EventAction.CREATED,
            eventKey = FeedMappingEventKey(
                workspaceId = workspaceId,
                feedComponentId = feedComponentId,
                subscriptionComponentId = subscriptionComponentId,
                sourceComponentId = sourceComponentId,
                sourceResourceId = sourceResourceId,
            ).makeKey(),
            payload = FeedMappingEvent(
                workspaceId = workspaceId,
                feedComponentId = feedComponentId,
                subscriptionComponentId = subscriptionComponentId,
                sourceComponentId = sourceComponentId,
                sourceResourceId = sourceResourceId,
            ),
        )

        fun deleted(
            workspaceId: String,
            feedComponentId: String,
            sourceResourceId: ResourceId,
            sourceComponentId: String,
            subscriptionComponentId: String,
        ) = EventRecord(
            eventAction = EventAction.REMOVED,
            eventKey = FeedMappingEventKey(
                workspaceId = workspaceId,
                feedComponentId = feedComponentId,
                subscriptionComponentId = subscriptionComponentId,
                sourceComponentId = sourceComponentId,
                sourceResourceId = sourceResourceId,
            ).makeKey(),
            payload = FeedMappingEvent(
                workspaceId = workspaceId,
                feedComponentId = feedComponentId,
                subscriptionComponentId = subscriptionComponentId,
                sourceComponentId = sourceComponentId,
                sourceResourceId = sourceResourceId,
            ),
        )
    }

}
