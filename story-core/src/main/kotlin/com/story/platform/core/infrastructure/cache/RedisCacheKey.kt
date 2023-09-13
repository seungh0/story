package com.story.platform.core.infrastructure.cache

import com.story.platform.core.infrastructure.redis.StringRedisKey
import java.time.Duration

data class RedisCacheKey(
    val cacheType: CacheType,
    val cacheKey: String,
) : StringRedisKey<RedisCacheKey, String> {

    override fun makeKeyString(): String = "${getCachePrefix(cacheType)}$cacheKey"

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

        fun getCachePrefix(cacheType: CacheType) = "cache:${cacheType.key}:"

        fun fromKeyString(cacheType: CacheType, keyString: String): RedisCacheKey {
            val prefix = getCachePrefix(cacheType)
            require(keyString.startsWith(prefix)) {
                "해당 keyString($keyString)은 cacheType($cacheType)에 해당하는 레디스 키가 아닙니다"
            }

            return RedisCacheKey(
                cacheType = cacheType,
                cacheKey = keyString.split(prefix)[1],
            )
        }
    }

}
