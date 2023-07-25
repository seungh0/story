package com.story.platform.core.domain.resource

import com.story.platform.core.domain.event.BaseEvent
import com.story.platform.core.domain.post.PostEvent
import com.story.platform.core.domain.subscription.SubscriptionEvent

/**
 * 매퍼 추가
 */
enum class ResourceId(
    val code: String,
    val description: String,
    val feedPayloadClazz: Class<out BaseEvent>?,
) {

    SUBSCRIPTIONS(code = "subscriptions", description = "구독", feedPayloadClazz = SubscriptionEvent::class.java),
    POSTS(code = "posts", description = "포스팅", feedPayloadClazz = PostEvent::class.java),
    FEEDS(code = "feeds", description = "피드", feedPayloadClazz = null),
    ;

    companion object {
        private val cachedResourceIdMap = mutableMapOf<String, ResourceId>()

        init {
            values().forEach { resourceId -> cachedResourceIdMap[resourceId.code.lowercase()] = resourceId }
        }

        fun findByCode(code: String): ResourceId {
            return cachedResourceIdMap[code.lowercase()]
                ?: throw ResourceNotExistsException(message = "해당하는 리소스($code)는 존재하지 않습니다")
        }
    }

}
