package com.story.core.infrastructure.cache

import com.story.core.common.logger.LoggerExtension.log
import org.springframework.stereotype.Component

@Component
class LayeredCacheManager(
    private val cacheManager: CacheManager,
) {

    suspend fun getCacheFromLayeredCache(
        cacheType: CacheType,
        cacheKey: String,
    ): Any? {
        val localCacheValue = cacheManager.getCache(
            cacheStrategy = CacheStrategy.LOCAL,
            cacheType = cacheType,
            cacheKey = cacheKey,
        )
        if (localCacheValue != null) {
            return localCacheValue
        }

        runCatching {
            val globalCacheValue = cacheManager.getCache(
                cacheStrategy = CacheStrategy.GLOBAL,
                cacheType = cacheType,
                cacheKey = cacheKey,
            )
            if (globalCacheValue != null) {
                cacheManager.refreshCache(
                    cacheStrategy = CacheStrategy.LOCAL,
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
        cacheManager.refreshCache(
            cacheStrategy = CacheStrategy.LOCAL,
            cacheType = cacheType,
            cacheKey = cacheKey,
            value = value
        )

        runCatching {
            cacheManager.refreshCache(
                cacheStrategy = CacheStrategy.GLOBAL,
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
        targetCacheStrategies: Set<CacheStrategy>,
    ) {
        if (CacheStrategy.LOCAL in targetCacheStrategies) {
            cacheManager.evict(
                cacheStrategy = CacheStrategy.LOCAL,
                cacheType = cacheType,
                cacheKey = cacheKey
            )
        }

        runCatching {
            if (CacheStrategy.GLOBAL in targetCacheStrategies) {
                cacheManager.evict(
                    cacheStrategy = CacheStrategy.GLOBAL,
                    cacheType = cacheType,
                    cacheKey = cacheKey
                )
            }
        }.onFailure { throwable ->
            log.error(throwable) { throwable.message }
        }
    }

    suspend fun evictAllCachesLayeredCache(
        cacheType: CacheType,
        targetCacheStrategies: Set<CacheStrategy> = CacheStrategy.entries.toSet(),
    ) {
        if (CacheStrategy.LOCAL in targetCacheStrategies) {
            cacheManager.evictAll(cacheStrategy = CacheStrategy.LOCAL, cacheType = cacheType)
        }

        if (CacheStrategy.GLOBAL in targetCacheStrategies) {
            runCatching {
                cacheManager.evictAll(cacheStrategy = CacheStrategy.GLOBAL, cacheType = cacheType)
            }.onFailure { throwable ->
                log.error(throwable) { throwable.message }
            }
        }
    }

}
