package com.story.platform.core.domain.feed.configuration

import com.story.platform.core.domain.event.EventAction
import com.story.platform.core.domain.resource.ResourceId

data class FeedMappingDisconnectRequest(
    val workspaceId: String,
    val feedComponentId: String,
    val resourceId: ResourceId,
    val componentId: String,
    val eventAction: EventAction,
    val targetResourceId: ResourceId,
    val targetComponentId: String,
) {

    fun toConfigurationPrimaryKey() = FeedMappingConfigurationPrimaryKey(
        workspaceId = workspaceId,
        feedComponentId = feedComponentId,
        sourceResourceId = resourceId,
        sourceComponentId = componentId,
        eventAction = eventAction,
        targetResourceId = targetResourceId,
        targetComponentId = targetComponentId,
    )

}
