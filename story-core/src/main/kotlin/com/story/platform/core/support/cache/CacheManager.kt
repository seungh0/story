package com.story.platform.core.support.cache

import com.story.platform.core.common.logger.LoggerExtension.log
import org.springframework.stereotype.Component

@Component
class CacheManager(
    private val cacheStrategyDelegator: CacheStrategyDelegator,
) {

    suspend fun getCacheFromLayeredCache(cacheType: CacheType, cacheKey: String): Any? {
        val localCacheValue = cacheStrategyDelegator.getCache(
            cacheStrategyType = CacheStrategyType.LOCAL,
            cacheType = cacheType,
            cacheKey = cacheKey,
        )
        if (localCacheValue != null) {
            return localCacheValue
        }

        runCatching {
            val globalCacheValue = cacheStrategyDelegator.getCache(
                cacheStrategyType = CacheStrategyType.GLOBAL,
                cacheType = cacheType,
                cacheKey = cacheKey,
            )
            if (globalCacheValue != null) {
                cacheStrategyDelegator.refreshCache(
                    cacheStrategyType = CacheStrategyType.LOCAL,
                    cacheType = cacheType,
                    cacheKey = cacheKey,
                    value = globalCacheValue
                )
                return globalCacheValue
            }
        }.onFailure { throwable ->
            log.error(throwable) { throwable.message }
        }
        return null
    }

    suspend fun refreshCacheLayeredCache(cacheType: CacheType, cacheKey: String, value: Any) {
        cacheStrategyDelegator.refreshCache(
            cacheStrategyType = CacheStrategyType.LOCAL,
            cacheType = cacheType,
            cacheKey = cacheKey,
            value = value
        )

        runCatching {
            cacheStrategyDelegator.refreshCache(
                cacheStrategyType = CacheStrategyType.GLOBAL,
                cacheType = cacheType,
                cacheKey = cacheKey,
                value = value
            )
        }.onFailure { throwable ->
            log.error(throwable) { throwable.message }
        }
    }

    suspend fun evictCacheLayeredCache(
        cacheType: CacheType,
        cacheKey: String,
        targetCacheStrategies: Set<CacheStrategyType>,
    ) {
        cacheStrategyDelegator.evict(
            cacheStrategyType = CacheStrategyType.LOCAL,
            cacheType = cacheType,
            cacheKey = cacheKey
        )

        runCatching {
            cacheStrategyDelegator.evict(
                cacheStrategyType = CacheStrategyType.GLOBAL,
                cacheType = cacheType,
                cacheKey = cacheKey
            )
        }.onFailure { throwable ->
            log.error(throwable) { throwable.message }
        }
    }

    suspend fun evictAllCachesLayeredCache(cacheType: CacheType) {
        cacheStrategyDelegator.evictAll(cacheStrategyType = CacheStrategyType.LOCAL, cacheType = cacheType)

        runCatching {
            cacheStrategyDelegator.evictAll(cacheStrategyType = CacheStrategyType.GLOBAL, cacheType = cacheType)
        }.onFailure { throwable ->
            log.error(throwable) { throwable.message }
        }
    }

}
