package com.story.core.domain.apikey

import com.story.core.infrastructure.cache.CacheEvict
import com.story.core.infrastructure.cache.CacheStrategy
import com.story.core.infrastructure.cache.CacheType
import com.story.core.infrastructure.cassandra.executeCoroutine
import com.story.core.infrastructure.cassandra.upsert
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.stereotype.Service

@Service
class ApiKeyModifier(
    private val workspaceApiKeyRepository: WorkspaceApiKeyRepository,
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
    private val apiKeyEventProducer: ApiKeyEventProducer,
) {

    @CacheEvict(
        cacheType = CacheType.API_KEY_REVERSE,
        key = "'apiKey:' + {#key}",
        targetCacheStrategies = [CacheStrategy.GLOBAL],
    )
    suspend fun patchApiKey(
        workspaceId: String,
        key: String,
        description: String?,
        status: ApiKeyStatus?,
    ) {
        val apiKey = findApiKey(workspaceId = workspaceId, key = key)

        apiKey.patch(
            description = description,
            status = status,
        )

        reactiveCassandraOperations.batchOps()
            .upsert(apiKey)
            .upsert(ApiKey.from(apiKey))
            .executeCoroutine()

        apiKeyEventProducer.publishEvent(
            apiKey = key,
            event = ApiKeyEvent.updated(workspaceApiKey = apiKey),
        )
    }

    private suspend fun findApiKey(workspaceId: String, key: String): WorkspaceApiKey {
        return workspaceApiKeyRepository.findById(
            WorkspaceApiKeyPrimaryKey(
                workspaceId = workspaceId,
                apiKey = key,
            )
        )
            ?: throw ApiKeyNotExistsException(message = "워크스페이스($workspaceId)에 등록되지 않은 API-Key($key) 입니다")
    }

}
