package com.story.core.domain.post

import com.story.core.domain.event.EventKey
import com.story.core.domain.event.EventKeyInvalidException

data class PostEventKey(
    val spaceId: String,
    val postId: Long,
) : EventKey {

    override fun makeKey(): String = "post::$spaceId::$postId"

    companion object {
        fun parse(eventKey: String): PostEventKey {
            if (!eventKey.startsWith("post::")) {
                throw EventKeyInvalidException("유효하지 않은 EventKey($eventKey)입니다")
            }

            try {
                val split = eventKey.split("::")
                return PostEventKey(
                    spaceId = split[1],
                    postId = split[2].toLong(),
                )
            } catch (exception: Exception) {
                throw EventKeyInvalidException("유효하지 않은 EventKey($eventKey)입니다")
            }
        }
    }

}
