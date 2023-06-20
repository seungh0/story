package com.story.platform.core.domain.subscription

import com.story.platform.core.infrastructure.redis.StringRedisKey
import java.time.Duration

data class SubscriberSequence(
    val workspaceId: String,
    val subscriptionType: SubscriptionType,
    val targetId: String,
) : StringRedisKey<SubscriberSequence, Long> {

    override fun makeKeyString(): String = "subscriber-sequence:$workspaceId:$subscriptionType:$targetId"

    override fun deserializeValue(value: String?): Long? = value?.toLongOrNull()

    override fun getTtl(): Duration? = null

    override fun serializeValue(value: Long): String = value.toString()

}
