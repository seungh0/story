package com.story.core.domain.feed.mapping

import com.story.core.domain.event.EventKey
import com.story.core.domain.resource.ResourceId

data class FeedMappingEventKey(
    val workspaceId: String,
    val feedComponentId: String,
    val sourceResourceId: ResourceId,
    val sourceComponentId: String,
    val subscriptionComponentId: String,
) : EventKey {

    override fun makeKey(): String =
        "workspaceId::$workspaceId::feed-component-id::$feedComponentId::source-resource-id::$sourceResourceId::source-component-id::$sourceComponentId::subscription-component-id::$subscriptionComponentId"

}
