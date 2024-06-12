package com.story.core.lib

import com.story.core.support.cache.CacheStrategy
import com.story.core.support.cache.CacheType
import com.story.core.support.cache.LayeredCacheManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.springframework.stereotype.Component

@Component
class CacheCleaner(
    private val cacheManager: LayeredCacheManager,
) {

    suspend fun cleanUp(cacheStrategies: Set<CacheStrategy>): List<Job> = coroutineScope {
        CacheType.entries.map { cacheType ->
            launch {
                cacheManager.evictAllCachesLayeredCache(cacheType = cacheType, targetCacheStrategies = cacheStrategies)
            }
        }
    }

}
