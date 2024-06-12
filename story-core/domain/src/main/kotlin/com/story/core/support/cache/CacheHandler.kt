package com.story.core.support.cache

interface CacheHandler {

    fun cacheStrategy(): CacheStrategy

    suspend fun getCache(cacheType: CacheType, cacheKey: String): Any?

    suspend fun refresh(cacheType: CacheType, cacheKey: String, value: Any)

    suspend fun evict(cacheType: CacheType, cacheKey: String)

    suspend fun evictAll(cacheType: CacheType)

}
