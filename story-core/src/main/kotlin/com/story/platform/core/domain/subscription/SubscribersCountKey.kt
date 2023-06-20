package com.story.platform.core.domain.subscription

import com.story.platform.core.infrastructure.redis.StringRedisKey
import java.time.Duration

data class SubscribersCountKey(
    val workspaceId: String,
    val componentId: String,
    val targetId: String,
) : StringRedisKey<SubscribersCountKey, Long> {

    override fun makeKeyString(): String = "subscribers-count:v1:$workspaceId:$componentId:$targetId"

    override fun deserializeValue(value: String?): Long? = value?.toLongOrNull()

    override fun getTtl(): Duration? = null

    override fun serializeValue(value: Long): String = value.toString()

}
