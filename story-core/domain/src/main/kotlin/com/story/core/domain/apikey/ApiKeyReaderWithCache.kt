package com.story.core.domain.apikey

import com.story.core.infrastructure.cache.CacheType
import com.story.core.infrastructure.cache.Cacheable
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class ApiKeyReaderWithCache(
    private val apiKeyReader: ApiKeyReader,
) {

    @Cacheable(
        cacheType = CacheType.API_KEY,
        key = "'apiKey:' + {#apiKey}",
    )
    suspend fun getApiKey(
        apiKey: String,
    ): Optional<ApiKey> {
        val entity = apiKeyReader.getApiKey(apiKey)
            ?: return Optional.empty()
        return Optional.of(entity)
    }

}
