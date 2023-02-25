package com.story.platform.core.support.cache

import com.story.platform.core.common.model.ReflectionType
import com.story.platform.core.common.utils.LoggerUtilsExtension.log
import com.story.platform.core.support.json.JsonUtils
import org.springframework.stereotype.Repository

@Repository
class GlobalCacheStrategy(
    private val redisCacheRepository: RedisCacheRepository,
) : CacheStrategy {

    override fun cacheStrategy(): CacheStrategyType = CacheStrategyType.GLOBAL

    override suspend fun getCache(cacheType: CacheType, cacheKey: String, returnType: ReflectionType): Any? {
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

        val globalCacheValue = JsonUtils.deserialize(
            returnType = returnType.returnType,
            actualType = returnType.actualType,
            jsonString = redisCacheValueJson
        )

        log.info { "글로벌 캐시로부터 데이터를 가져옵니다 [key:$cacheKey value: $globalCacheValue]" }

        return globalCacheValue
    }

    override suspend fun refresh(cacheType: CacheType, cacheKey: String, value: Any) {
        if (!cacheType.enableGlobalCache()) {
            return
        }
        redisCacheRepository.setCache(cacheType = cacheType, cacheKey = cacheKey, value = JsonUtils.toJson(value))
        log.info { "글로벌 캐시를 갱신합니다 [cacheType: $cacheType keyString: $cacheKey value: $value]" }
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

}
