package com.story.platform.core.domain.subscription

import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.infrastructure.redis.StringRedisKey
import java.time.Duration

data class SubscribersCountKey(
    val serviceType: ServiceType,
    val subscriptionType: SubscriptionType,
    val targetId: String,
) : StringRedisKey<SubscribersCountKey, Long> {

    override fun makeKeyString(): String = "subscribers-count:v1:$serviceType:$subscriptionType:$targetId"

    override fun deserializeValue(value: String?): Long? = value?.toLongOrNull()

    override fun getTtl(): Duration? = null

    override fun serializeValue(value: Long): String = value.toString()

}
