package com.story.core.infrastructure.cache

import com.story.core.common.logger.LoggerExtension.log
import org.springframework.stereotype.Component

@Component
class CacheManager(
    private val cacheHandlerDelegator: CacheHandlerDelegator,
) {

    suspend fun getCacheFromLayeredCache(
        cacheType: CacheType,
        cacheKey: String,
    ): Any? {
        val localCacheValue = cacheHandlerDelegator.getCache(
            cacheStrategy = CacheStrategy.LOCAL,
            cacheType = cacheType,
            cacheKey = cacheKey,
        )
        if (localCacheValue != null) {
            return localCacheValue
        }

        runCatching {
            val globalCacheValue = cacheHandlerDelegator.getCache(
                cacheStrategy = CacheStrategy.GLOBAL,
                cacheType = cacheType,
                cacheKey = cacheKey,
            )
            if (globalCacheValue != null) {
                cacheHandlerDelegator.refreshCache(
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
        cacheHandlerDelegator.refreshCache(
            cacheStrategy = CacheStrategy.LOCAL,
            cacheType = cacheType,
            cacheKey = cacheKey,
            value = value
        )

        runCatching {
            cacheHandlerDelegator.refreshCache(
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
            cacheHandlerDelegator.evict(
                cacheStrategy = CacheStrategy.LOCAL,
                cacheType = cacheType,
                cacheKey = cacheKey
            )
        }

        runCatching {
            if (CacheStrategy.GLOBAL in targetCacheStrategies) {
                cacheHandlerDelegator.evict(
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
            cacheHandlerDelegator.evictAll(cacheStrategy = CacheStrategy.LOCAL, cacheType = cacheType)
        }

        if (CacheStrategy.GLOBAL in targetCacheStrategies) {
            runCatching {
                cacheHandlerDelegator.evictAll(cacheStrategy = CacheStrategy.GLOBAL, cacheType = cacheType)
            }.onFailure { throwable ->
                log.error(throwable) { throwable.message }
            }
        }
    }

}
