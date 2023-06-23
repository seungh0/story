package com.story.platform.core.helper

import com.story.platform.core.support.cache.CacheManager
import com.story.platform.core.support.cache.CacheType
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.springframework.stereotype.Component

@Component
class CacheCleaner(
    private val cacheManager: CacheManager,
) {

    suspend fun cleanUp(): List<Job> = coroutineScope {
        CacheType.values().map { cacheType ->
            launch {
                cacheManager.evictAllCachesLayeredCache(cacheType = cacheType)
            }
        }
    }

}
