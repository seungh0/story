package com.story.core.support.cache

import com.story.core.support.redis.StringRedisKey
import java.time.Duration

data class GlobalCacheKey(
    val cacheType: CacheType,
    val cacheKey: String,
) : StringRedisKey<GlobalCacheKey, String> {

    override fun makeKeyString(): String = "${getCachePrefix(cacheType)}$cacheKey"

    override fun serializeValue(value: String): String = value

    override fun deserializeValue(value: String?): String? = value

    override fun getTtl(): Duration? = cacheType.globalCacheTtl

    companion object {
        fun of(
            cacheType: CacheType,
            cacheKey: String,
        ) = GlobalCacheKey(
            cacheType = cacheType,
            cacheKey = cacheKey,
        )

        fun getCachePrefix(cacheType: CacheType) = "cache:${cacheType.key}:"

        fun fromKeyString(cacheType: CacheType, keyString: String): GlobalCacheKey {
            val prefix = getCachePrefix(cacheType)
            require(keyString.startsWith(prefix)) {
                "해당 keyString($keyString)은 cacheType($cacheType)에 해당하는 레디스 키가 아닙니다"
            }

            return GlobalCacheKey(
                cacheType = cacheType,
                cacheKey = keyString.split(prefix)[1],
            )
        }
    }

}
