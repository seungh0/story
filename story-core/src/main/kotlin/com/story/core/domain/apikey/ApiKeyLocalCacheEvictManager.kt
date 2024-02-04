package com.story.core.domain.apikey

import com.story.core.common.logger.LoggerExtension.log
import com.story.core.infrastructure.cache.CacheEvict
import com.story.core.infrastructure.cache.CacheStrategy
import com.story.core.infrastructure.cache.CacheType
import org.springframework.stereotype.Service

@Service
class ApiKeyLocalCacheEvictManager {

    @CacheEvict(
        cacheType = CacheType.API_KEY_REVERSE,
        key = "'apiKey:' + {#apiKey}",
        targetCacheStrategies = [CacheStrategy.LOCAL],
    )
    suspend fun evictApiKey(
        apiKey: String,
    ) {
        log.info { "ApiKey 캐시가 만료됩니다 [apiKey: $apiKey]" }
    }

}
