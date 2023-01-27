package com.story.platform.core.domain.subscription

import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.support.redis.StringRedisKey
import java.time.Duration

data class SubscriptionIdGenerateKey(
    val serviceType: ServiceType,
    val subscriptionType: String,
    val targetId: String,
) : StringRedisKey<SubscriptionIdGenerateKey, Long> {

    override fun getKey(): String = "subscription:st:$serviceType:s:$subscriptionType:t:$targetId"

    override fun deserializeValue(value: String?): Long? = value?.toLongOrNull()

    override fun getTtl(): Duration? = null

    override fun serializeValue(value: Long): String = value.toString()

}
