package com.story.core.domain.event

import com.story.core.domain.resource.ResourceId

object EventKeyGenerator {

    fun subscription(subscriberId: String, targetId: String) = "subscription::$subscriberId::$targetId"
    fun post(spaceId: String, postId: Long) = "post::$spaceId::$postId"
    fun component(componentId: String) = "component::$componentId"
    fun authenticationKey(authenticationKey: String) = "authentication-key::$authenticationKey"
    fun purge(resourceId: ResourceId, componentId: String) = "purge::${resourceId.code}::$componentId"
    fun workspace(workspaceId: String) = "workspace:$workspaceId"
    fun reaction(spaceId: String, accountId: String) = "reaction::$spaceId::$accountId"

}
