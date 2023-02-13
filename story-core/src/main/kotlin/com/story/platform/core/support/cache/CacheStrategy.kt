package com.story.platform.core.support.cache

import com.story.platform.core.common.model.ReflectionType

interface CacheStrategy {

    fun cacheStrategy(): CacheStrategyType

    suspend fun getCache(cacheType: CacheType, cacheKey: String, returnType: ReflectionType): Any?

    suspend fun refresh(cacheType: CacheType, cacheKey: String, value: Any)

    suspend fun evict(cacheType: CacheType, cacheKey: String)

}
