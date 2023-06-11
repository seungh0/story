package com.story.platform.core.support.cache

interface CacheStrategy {

    fun cacheStrategy(): CacheStrategyType

    suspend fun getCache(cacheType: CacheType, cacheKey: String): Any?

    suspend fun refresh(cacheType: CacheType, cacheKey: String, value: Any)

    suspend fun evict(cacheType: CacheType, cacheKey: String)

    suspend fun evictAll(cacheType: CacheType)

}
