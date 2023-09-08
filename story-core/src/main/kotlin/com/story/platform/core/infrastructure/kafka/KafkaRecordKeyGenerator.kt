package com.story.platform.core.infrastructure.kafka

import com.story.platform.core.domain.resource.ResourceId

object KafkaRecordKeyGenerator {

    fun feed(eventKey: String, slotId: Long) = "$eventKey:$slotId"

    fun component(workspaceId: String, resourceId: ResourceId, componentId: String) =
        "$workspaceId:$resourceId:$componentId"

    fun subscription(workspaceId: String, componentId: String, subscriberId: String) =
        "$workspaceId:$componentId:$subscriberId"

    fun post(workspaceId: String, componentId: String, postId: Long) = "$workspaceId:$componentId:$postId"

    fun authentication(authenticationKey: String) = authenticationKey

    fun workspace(workspaceId: String) = workspaceId

}
