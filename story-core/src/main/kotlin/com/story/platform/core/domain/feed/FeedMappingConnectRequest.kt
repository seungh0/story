package com.story.platform.core.domain.feed

import com.story.platform.core.domain.event.EventAction
import com.story.platform.core.domain.resource.ResourceId

data class FeedMappingConnectRequest(
    val workspaceId: String,
    val feedComponentId: String,
    val resourceId: ResourceId,
    val componentId: String,
    val eventAction: EventAction,
    val targetResourceId: ResourceId,
    val targetComponentId: String,
    val description: String,
) {

    fun toConfigurationKey() = FeedMappingConfigurationPrimaryKey(
        workspaceId = workspaceId,
        feedComponentId = feedComponentId,
        sourceResourceId = resourceId,
        sourceComponentId = componentId,
        eventAction = eventAction,
        targetResourceId = targetResourceId,
        targetComponentId = targetComponentId,
    )

    fun toConfiguration() = FeedMappingConfiguration.of(
        workspaceId = workspaceId,
        feedComponentId = feedComponentId,
        sourceResourceId = resourceId,
        sourceComponentId = componentId,
        eventAction = eventAction,
        targetResourceId = targetResourceId,
        targetComponentId = targetComponentId,
        description = description,
    )

}
