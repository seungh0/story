package com.story.core.domain.apikey

import com.story.core.support.cache.CacheEvict
import com.story.core.support.cache.CacheStrategy
import com.story.core.support.cache.CacheType
import org.springframework.stereotype.Service

@Service
class ApiKeyLocalCacheEvictManager {

    @CacheEvict(
        cacheType = CacheType.API_KEY,
        key = "'apiKey:' + {#apiKey}",
        targetCacheStrategies = [CacheStrategy.LOCAL],
    )
    suspend fun evictApiKey(
        apiKey: String,
    ) {
    }

}
