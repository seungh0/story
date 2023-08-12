package com.story.platform.core.support.cache

import com.story.platform.core.common.json.toJson
import com.story.platform.core.common.json.toObject
import com.story.platform.core.common.logger.LoggerExtension.log
import org.springframework.stereotype.Repository

@Repository
class GlobalCacheStrategy(
    private val redisCacheRepository: RedisCacheRepository,
) : CacheStrategy {

    override fun cacheStrategy(): CacheStrategyType = CacheStrategyType.GLOBAL

    override suspend fun getCache(cacheType: CacheType, cacheKey: String): Any? {
        if (!cacheType.enableGlobalCache() || redisCacheRepository.isEarlyRecomputedRequired(
                cacheType = cacheType,
                cacheKey = cacheKey
            )
        ) {
            return null
        }

        val redisCacheValueJson = redisCacheRepository.getCache(cacheType = cacheType, cacheKey = cacheKey)
        if (redisCacheValueJson.isNullOrBlank()) {
            return null
        }

        val globalCacheValue = redisCacheValueJson.toObject(cacheType.typeReference)
        log.debug { "글로벌 캐시로부터 데이터를 가져옵니다 [key:$cacheKey value: $globalCacheValue]" }
        return globalCacheValue
    }

    override suspend fun refresh(cacheType: CacheType, cacheKey: String, value: Any) {
        if (!cacheType.enableGlobalCache()) {
            return
        }
        redisCacheRepository.setCache(cacheType = cacheType, cacheKey = cacheKey, value = value.toJson())
        log.debug { "글로벌 캐시를 갱신합니다 [cacheType: $cacheType keyString: $cacheKey value: $value]" }
    }

    override suspend fun evict(cacheType: CacheType, cacheKey: String) {
        if (!cacheType.enableGlobalCache()) {
            return
        }
        redisCacheRepository.evict(cacheType = cacheType, cacheKey = cacheKey)
        if (log.isDebugEnabled) {
            log.debug { "글로벌 캐시를 삭제합니다 [cacheType: $cacheType keyString: $cacheKey]" }
        }
    }

    override suspend fun evictAll(cacheType: CacheType) {
        if (!cacheType.enableGlobalCache()) {
            return
        }
        redisCacheRepository.evictAll(cacheType = cacheType)
    }

}
