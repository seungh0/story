package com.story.core.domain.subscription

import com.story.core.domain.event.EventKey
import com.story.core.domain.event.EventKeyInvalidException

data class SubscriptionEventKey(
    val subscriberId: String,
    val targetId: String,
) : EventKey {

    override fun makeKey(): String = "subscription::$subscriberId::$targetId"

    companion object {
        fun parse(eventKey: String): SubscriptionEventKey {
            if (!eventKey.startsWith("subscription::")) {
                throw EventKeyInvalidException("유효하지 않은 EventKey($eventKey)입니다")
            }

            try {
                val split = eventKey.split("::")
                return SubscriptionEventKey(
                    subscriberId = split[1],
                    targetId = split[2],
                )
            } catch (exception: Exception) {
                throw EventKeyInvalidException("유효하지 않은 EventKey($eventKey)입니다")
            }
        }
    }

}
