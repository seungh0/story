package com.story.core.domain.apikey

import com.story.core.support.cache.CacheEvict
import com.story.core.support.cache.CacheType
import org.springframework.stereotype.Service

@Service
class ApiKeyModifier(
    private val apiKeyWriteRepository: ApiKeyWriteRepository,
) {

    @CacheEvict(
        cacheType = CacheType.API_KEY,
        key = "'apiKey:' + {#key}",
    )
    suspend fun patchApiKey(
        workspaceId: String,
        key: String,
        description: String?,
        status: ApiKeyStatus?,
    ): ApiKey {
        return apiKeyWriteRepository.partialUpdate(
            workspaceId = workspaceId,
            key = key,
            description = description,
            status = status,
        )
    }

}
