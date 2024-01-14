package com.story.core.infrastructure.kafka

import com.story.core.domain.resource.ResourceId

object KafkaRecordKeyGenerator {

    fun feed(
        workspaceId: String,
        feedComponentId: String,
        eventKey: String,
        slotId: Long,
    ) = ":$workspaceId:$feedComponentId:$eventKey:$slotId"

    fun component(workspaceId: String, resourceId: ResourceId, componentId: String) =
        "$workspaceId:$resourceId:$componentId"

    fun subscription(workspaceId: String, componentId: String, subscriberId: String) =
        "$workspaceId:$componentId:$subscriberId"

    fun post(workspaceId: String, componentId: String, postId: Long) = "$workspaceId:$componentId:$postId"

    fun authentication(authenticationKey: String) = authenticationKey

    fun workspace(workspaceId: String) = workspaceId

}
