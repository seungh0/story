package com.story.platform.core.domain.event

import com.story.platform.core.domain.resource.ResourceId

object EventKeyGenerator {

    fun subscription(subscriberId: String, targetId: String) = "subscription::$subscriberId::$targetId"
    fun post(spaceId: String, postId: Long) = "post::$spaceId::$postId"
    fun component(componentId: String) = "component::$componentId"
    fun authenticationKey(authenticationKey: String) = "authentication-key::$authenticationKey"
    fun purge(workspaceId: String, resourceId: ResourceId, componentId: String) =
        "purge::$workspaceId::${resourceId.code}::$componentId"

}
