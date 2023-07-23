package com.story.platform.core.domain.event

object EventKeyGenerator {

    fun subscription(subscriberId: String, targetId: String) = "subscription::$subscriberId::$targetId"

    fun post(spaceId: String, postId: Long) = "post::$spaceId::$postId"

}
