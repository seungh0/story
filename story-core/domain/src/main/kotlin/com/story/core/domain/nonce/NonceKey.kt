package com.story.core.domain.nonce

import com.story.core.infrastructure.redis.StringRedisKey
import java.time.Duration

data class NonceKey(
    val nonce: String,
) : StringRedisKey<NonceKey, Long> {

    override fun makeKeyString(): String = "nonce:v1:$nonce"

    override fun deserializeValue(value: String?): Long? = value?.toLongOrNull()

    override fun getTtl(): Duration? = Duration.ofHours(1)

    override fun serializeValue(value: Long): String = value.toString()

}
