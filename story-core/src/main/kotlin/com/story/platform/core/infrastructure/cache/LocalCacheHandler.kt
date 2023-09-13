package com.story.platform.core.infrastructure.cache

import com.story.platform.core.common.logger.LoggerExtension.log
import org.springframework.cache.CacheManager
import org.springframework.stereotype.Repository

@Repository
class LocalCacheHandler(
    private val cacheManager: CacheManager,
) : CacheHandler {

    override fun cacheStrategy(): CacheStrategy = CacheStrategy.LOCAL

    override suspend fun getCache(cacheType: CacheType, cacheKey: String): Any? {
        if (!cacheType.enableLocalCache()) {
            return null
        }
        val cache = cacheManager.getCache(cacheType.key)
            ?: return null

        val cacheValueWrapper = cache[cacheKey]
            ?: return null

        val cacheValue = cacheValueWrapper.get()

        if (cacheValue != null) {
            log.debug { "로컬 캐시로부터 데이터를 가져옵니다 [key:$cacheKey value: $cacheValue]" }
        }

        return cacheValue
    }

    override suspend fun refresh(cacheType: CacheType, cacheKey: String, value: Any) {
        if (!cacheType.enableLocalCache()) {
            return
        }
        cacheManager.getCache(cacheType.key)?.put(cacheKey, value)
        log.debug { "로컬 캐시를 갱신합니다 [cacheType: $cacheType keyString: $cacheKey value: $value]" }
    }

    override suspend fun evict(cacheType: CacheType, cacheKey: String) {
        if (!cacheType.enableLocalCache()) {
            return
        }
        cacheManager.getCache(cacheType.key)?.evictIfPresent(cacheKey)
        log.debug { "로컬 캐시를 삭제합니다 [cacheType: $cacheType keyString: $cacheKey]" }
    }

    override suspend fun evictAll(cacheType: CacheType) {
        if (!cacheType.enableLocalCache()) {
            return
        }
        cacheManager.getCache(cacheType.key)?.clear()
    }

}
