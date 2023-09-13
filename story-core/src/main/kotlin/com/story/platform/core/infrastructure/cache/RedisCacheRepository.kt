package com.story.platform.core.infrastructure.cache

import java.time.Duration

interface RedisCacheRepository {

    suspend fun getCache(
        cacheType: CacheType,
        cacheKey: String,
    ): String?

    suspend fun setCache(
        cacheType: CacheType,
        cacheKey: String,
        value: String,
    )

    suspend fun evict(
        cacheType: CacheType,
        cacheKey: String,
    )

    suspend fun isEarlyRecomputedRequired(
        cacheType: CacheType,
        cacheKey: String,
    ): Boolean

    suspend fun getTtl(
        cacheType: CacheType,
        cacheKey: String,
    ): Duration

    suspend fun evictAll(cacheType: CacheType)

}
