package com.story.platform.core.domain.feed

import com.story.platform.core.domain.event.EventAction
import com.story.platform.core.domain.resource.ResourceId

data class FeedMappingConnectRequest(
    val workspaceId: String,
    val resourceId: ResourceId,
    val componentId: String,
    val eventAction: EventAction,
    val subscriptionComponentId: String,
    val description: String,
) {

    fun toConfigurationKey() = FeedMappingConfigurationPrimaryKey(
        workspaceId = workspaceId,
        resourceId = resourceId,
        componentId = componentId,
        eventAction = eventAction,
        subscriptionComponentId = subscriptionComponentId,
    )

    fun toConfiguration() = FeedMappingConfiguration.of(
        workspaceId = workspaceId,
        resourceId = resourceId,
        componentId = componentId,
        eventAction = eventAction,
        subscriptionComponentId = subscriptionComponentId,
        description = description,
    )

}
