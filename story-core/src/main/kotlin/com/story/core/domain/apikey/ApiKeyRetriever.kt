package com.story.core.domain.apikey

import com.story.core.infrastructure.cache.CacheType
import com.story.core.infrastructure.cache.Cacheable
import org.springframework.stereotype.Service

@Service
class ApiKeyRetriever(
    private val apiKeyRepository: ApiKeyRepository,
) {

    @Cacheable(
        cacheType = CacheType.API_KEY_REVERSE,
        key = "'apiKey:' + {#apiKey}",
    )
    suspend fun getApiKey(
        apiKey: String,
    ): ApiKeyResponse {
        return ApiKeyResponse.of(
            apiKeyRepository.findById(apiKey)
                ?: throw ApiKeyNotExistsException(message = "등록되지 않은 APIKey($apiKey) 입니다")
        )
    }

}
