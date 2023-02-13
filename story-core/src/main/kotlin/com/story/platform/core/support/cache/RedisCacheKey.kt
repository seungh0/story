package com.story.platform.core.support.cache

import com.story.platform.core.support.redis.StringRedisKey
import java.time.Duration

data class RedisCacheKey(
    val cacheType: CacheType,
    val cacheKey: String,
) : StringRedisKey<RedisCacheKey, String> {

    override fun getKey(): String = "cache:${cacheType.key}:${cacheKey}"

    override fun serializeValue(value: String): String = value

    override fun deserializeValue(value: String?): String? = value

    override fun getTtl(): Duration? = cacheType.globalCacheTtl

    companion object {
        fun of(
            cacheType: CacheType,
            cacheKey: String,
        ) = RedisCacheKey(
            cacheType = cacheType,
            cacheKey = cacheKey,
        )
    }

}
