package com.story.platform.core.helper

import com.story.platform.core.support.cache.CacheManager
import com.story.platform.core.support.cache.CacheType
import org.springframework.stereotype.Component

@Component
class CacheCleaner(
    private val cacheManager: CacheManager,
) {

    suspend fun cleanUp() {
        for (cacheType in CacheType.values()) {
            cacheManager.evictAllCachesLayeredCache(cacheType = cacheType)
        }
    }

}
