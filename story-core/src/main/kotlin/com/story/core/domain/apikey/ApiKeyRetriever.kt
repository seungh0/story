package com.story.core.domain.apikey

import com.story.core.infrastructure.cache.CacheType
import com.story.core.infrastructure.cache.Cacheable
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class ApiKeyRetriever(
    private val apiKeyRepository: ApiKeyRepository,
) {

    @Cacheable(
        cacheType = CacheType.API_KEY,
        key = "'apiKey:' + {#apiKey}",
    )
    suspend fun getApiKey(
        apiKey: String,
    ): Optional<ApiKey> {
        val entity = apiKeyRepository.findById(apiKey)
            ?: return Optional.empty()
        return Optional.of(ApiKey.of(apiKey = entity))
    }

}
