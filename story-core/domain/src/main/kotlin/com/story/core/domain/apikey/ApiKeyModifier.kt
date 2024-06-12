package com.story.core.domain.apikey

import com.story.core.support.cache.CacheEvict
import com.story.core.support.cache.CacheStrategy
import com.story.core.support.cache.CacheType
import org.springframework.stereotype.Service

@Service
class ApiKeyModifier(
    private val apiKeyWriteRepository: ApiKeyWriteRepository,
    private val apiKeyEventProducer: ApiKeyEventProducer,
) {

    @CacheEvict(
        cacheType = CacheType.API_KEY,
        key = "'apiKey:' + {#key}",
        targetCacheStrategies = [CacheStrategy.GLOBAL],
    )
    suspend fun patchApiKey(
        workspaceId: String,
        key: String,
        description: String?,
        status: ApiKeyStatus?,
    ) {
        val apiKey = apiKeyWriteRepository.update(
            workspaceId = workspaceId,
            key = key,
            description = description,
            status = status,
        )

        apiKeyEventProducer.publishEvent(
            apiKey = key,
            event = ApiKeyEvent.updated(apiKey),
        )
    }

}
