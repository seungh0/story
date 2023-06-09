package com.story.platform.core.domain.subscription

import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.infrastructure.redis.StringRedisKey
import java.time.Duration

data class SubscriptionsCountKey(
    val serviceType: ServiceType,
    val subscriptionType: SubscriptionType,
    val subscriberId: String,
) : StringRedisKey<SubscriptionsCountKey, Long> {

    override fun makeKeyString(): String = "subscriptions-count:v1:$serviceType:$subscriptionType:$subscriberId"

    override fun deserializeValue(value: String?): Long? = value?.toLongOrNull()

    override fun getTtl(): Duration? = null

    override fun serializeValue(value: Long): String = value.toString()

}
