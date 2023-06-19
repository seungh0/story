package com.story.platform.core.helper

import com.story.platform.core.support.cache.CacheManager
import com.story.platform.core.support.cache.CacheType
import com.story.platform.core.support.coroutine.IOBound
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Component

@Component
class CacheCleaner(
    private val cacheManager: CacheManager,

    @IOBound
    private val dispatcher: CoroutineDispatcher,
) {

    suspend fun cleanUp(): List<Job> {
        return withContext(dispatcher) {
            CacheType.values().map { cacheType ->
                launch {
                    cacheManager.evictAllCachesLayeredCache(cacheType = cacheType)
                }
            }
        }
    }

}
