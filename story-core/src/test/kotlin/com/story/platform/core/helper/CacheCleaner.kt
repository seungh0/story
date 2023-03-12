package com.story.platform.core.helper

import com.story.platform.core.support.cache.CacheManager
import com.story.platform.core.support.cache.CacheType
import io.kotest.common.runBlocking
import org.springframework.stereotype.Component

@Component
class CacheCleaner(
    private val cacheManager: CacheManager,
) {

    fun cleanUp() {
        runBlocking {
            for (cacheType in CacheType.values()) {
                cacheManager.evictAllCachesLayeredCache(cacheType = cacheType)
            }
        }
    }

}
