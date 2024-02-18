package com.story.core.infrastructure.kafka

import com.story.core.domain.post.PostKey
import com.story.core.domain.resource.ResourceId

object KafkaRecordKeyGenerator {

    fun feed(
        workspaceId: String,
        feedComponentId: String,
        eventKey: String,
        slotId: Long,
    ) = ":$workspaceId:$feedComponentId:$eventKey:$slotId"

    fun feedMapping(
        workspaceId: String,
        sourceResourceId: ResourceId,
        sourceComponentId: String,
    ) = ":$workspaceId:$sourceResourceId:$sourceComponentId"

    fun component(workspaceId: String, resourceId: ResourceId, componentId: String) =
        "$workspaceId:$resourceId:$componentId"

    fun subscription(workspaceId: String, componentId: String, subscriberId: String) =
        "$workspaceId:$componentId:$subscriberId"

    fun post(workspaceId: String, componentId: String, postId: PostKey) =
        "$workspaceId:$componentId:${postId.serialize()}"

    fun apiKey(key: String) = key

    fun workspace(workspaceId: String) = workspaceId

}
