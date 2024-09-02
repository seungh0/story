package com.story.core.support.kafka

import com.story.core.domain.post.PostId
import com.story.core.domain.resource.ResourceId

object KafkaRecordKeyGenerator {

    fun feed(
        workspaceId: String,
        componentId: String,
        itemResourceId: ResourceId,
        itemComponentId: String,
        itemId: String,
    ) = ":$workspaceId:$componentId:${itemResourceId.code}:$itemComponentId:$itemId"

    fun feedMapping(
        workspaceId: String,
        sourceResourceId: ResourceId,
        sourceComponentId: String,
    ) = ":$workspaceId:$sourceResourceId:$sourceComponentId"

    fun component(workspaceId: String, resourceId: ResourceId, componentId: String) =
        "$workspaceId:$resourceId:$componentId"

    fun subscription(workspaceId: String, componentId: String, subscriberId: String) =
        "$workspaceId:$componentId:$subscriberId"

    fun post(workspaceId: String, componentId: String, postId: PostId) =
        "$workspaceId:$componentId:${postId.serialize()}"

    fun apiKey(key: String) = key

    fun workspace(workspaceId: String) = workspaceId

}
