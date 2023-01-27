package com.story.platform.core.domain.subscription

import com.story.platform.core.support.redis.StringRedisKey
import java.time.Duration

data class SubscriptionSequenceKey(
    val serviceType: com.story.platform.core.common.enums.ServiceType,
    val subscriptionType: String,
    val targetId: String,
) : StringRedisKey<SubscriptionSequenceKey, Long> {

    override fun getKey(): String = "s:service:$serviceType:subscription:$subscriptionType:target:$targetId"

    override fun deserializeValue(value: String?): Long? = value?.toLongOrNull()

    override fun getTtl(): Duration? = null

    override fun serializeValue(value: Long): String = value.toString()

}
