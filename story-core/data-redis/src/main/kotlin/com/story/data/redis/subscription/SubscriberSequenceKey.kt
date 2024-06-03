package com.story.data.redis.subscription

import com.story.core.infrastructure.redis.StringRedisKey
import java.time.Duration

data class SubscriberSequenceKey(
    val workspaceId: String,
    val componentId: String,
    val targetId: String,
) : StringRedisKey<SubscriberSequenceKey, Long> {

    override fun makeKeyString(): String = "subscriber-sequence:v1:$workspaceId:$componentId:$targetId"

    override fun deserializeValue(value: String?): Long? = value?.toLongOrNull()

    override fun getTtl(): Duration? = null

    override fun serializeValue(value: Long): String = value.toString()

}
