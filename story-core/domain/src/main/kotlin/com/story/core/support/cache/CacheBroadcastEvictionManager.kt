package com.story.core.support.cache

import org.springframework.stereotype.Component

@Component
class CacheBroadcastEvictionManager(
    private val layeredCacheManager: LayeredCacheManager,
    private val cacheBroadcastEvictionProducer: CacheBroadcastEvictionProducer,
) {

    suspend fun evictCacheByCacheKey(
        cacheType: CacheType,
        targetCacheStrategies: Set<CacheStrategy> = CacheStrategy.entries.toSet(),
        cacheKey: String,
    ) {
        if (CacheStrategy.GLOBAL in targetCacheStrategies) {
            layeredCacheManager.evictCacheLayeredCache(
                cacheType = cacheType,
                cacheKey = cacheKey,
                targetCacheStrategies = setOf(CacheStrategy.GLOBAL)
            )
        }

        if (CacheStrategy.LOCAL in targetCacheStrategies) {
            cacheBroadcastEvictionProducer.targetKey(
                cacheType = cacheType,
                cacheKey = cacheKey,
            )
        }
    }

    @JvmOverloads
    suspend fun evictAllCacheEntries(
        cacheType: CacheType,
        targetCacheStrategies: Set<CacheStrategy> = CacheStrategy.entries.toSet(),
    ) {
        if (CacheStrategy.GLOBAL in targetCacheStrategies) {
            layeredCacheManager.evictAllCachesLayeredCache(
                cacheType = cacheType,
                targetCacheStrategies = setOf(CacheStrategy.GLOBAL)
            )
        }

        if (CacheStrategy.LOCAL in targetCacheStrategies) {
            cacheBroadcastEvictionProducer.allEntries(cacheType = cacheType)
        }
    }

}
