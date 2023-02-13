package com.story.platform.core.support.cache

import com.story.platform.core.common.model.ReflectionType
import com.story.platform.core.common.utils.LoggerUtilsExtension.log
import org.springframework.cache.CacheManager
import org.springframework.stereotype.Repository

@Repository
class LocalCacheStrategy(
    private val cacheManager: CacheManager,
) : CacheStrategy {

    override fun cacheStrategy(): CacheStrategyType = CacheStrategyType.LOCAL

    override suspend fun getCache(cacheType: CacheType, cacheKey: String, returnType: ReflectionType): Any? {
        if (!cacheType.enableLocalCache()) {
            return null
        }
        val cache = cacheManager.getCache(cacheType.key)
        if (cache == null) {
            return null
        }

        val cacheValueWrapper = cache.get(cacheKey)
        if (cacheValueWrapper == null) {
            return null
        }

        val cacheValue = cacheValueWrapper.get()

        if (cacheValue != null && log.isDebugEnabled) {
            log.debug { "로컬 캐시로부터 데이터를 가져옵니다 [key:$cacheKey value: $cacheValue]" }
        }

        return cacheValue
    }

    override suspend fun refresh(cacheType: CacheType, cacheKey: String, value: Any) {
        if (!cacheType.enableLocalCache()) {
            return
        }
        cacheManager.getCache(cacheType.key)?.put(cacheKey, value)
        if (log.isDebugEnabled) {
            log.debug { "로컬 캐시를 갱신합니다 [cacheType: $cacheType keyString: $cacheKey value: $value]" }
        }
    }

    override suspend fun evict(cacheType: CacheType, cacheKey: String) {
        if (!cacheType.enableLocalCache()) {
            return
        }
        cacheManager.getCache(cacheType.key)?.evictIfPresent(cacheKey)
        if (log.isDebugEnabled) {
            log.debug { "로컬 캐시를 삭제합니다 [cacheType: $cacheType keyString: $cacheKey]" }
        }
    }

}
