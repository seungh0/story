package com.story.platform.core.domain.feed

import com.story.platform.core.domain.event.EventAction
import com.story.platform.core.domain.resource.ResourceId

data class FeedMappingModifyRequest(
    val workspaceId: String,
    val resourceId: ResourceId,
    val componentId: String,
    val eventAction: EventAction,
    val subscriptionComponentId: String,
    val description: String?,
    val status: FeedMappingConfigurationStatus?,
) {

    fun toConfigurationKey() = FeedMappingConfigurationPrimaryKey(
        workspaceId = workspaceId,
        resourceId = resourceId,
        componentId = componentId,
        eventAction = eventAction,
        subscriptionComponentId = subscriptionComponentId,
    )

}
