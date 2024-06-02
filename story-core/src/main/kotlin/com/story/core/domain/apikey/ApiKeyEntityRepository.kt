package com.story.core.domain.apikey

import com.story.core.domain.apikey.storage.ApiKeyCassandraRepository
import com.story.core.domain.apikey.storage.ApiKeyEntity
import com.story.core.domain.apikey.storage.WorkspaceApiKey
import com.story.core.domain.apikey.storage.WorkspaceApiKeyCassandraRepository
import com.story.core.domain.apikey.storage.WorkspaceApiKeyPrimaryKey
import com.story.core.infrastructure.cassandra.executeCoroutine
import com.story.core.infrastructure.cassandra.upsert
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.stereotype.Repository

@Repository
class ApiKeyEntityRepository(
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
    private val workspaceApiKeyCassandraRepository: WorkspaceApiKeyCassandraRepository,
    private val apiKeyCassandraRepository: ApiKeyCassandraRepository,
) : ApiKeyReadRepository, ApiKeyWriteRepository {

    override suspend fun create(workspaceId: String, key: String, description: String): ApiKey {
        val entity = WorkspaceApiKey.of(
            workspaceId = workspaceId,
            apiKey = key,
            description = description,
        )

        reactiveCassandraOperations.batchOps()
            .upsert(entity)
            .upsert(ApiKeyEntity.from(entity))
            .executeCoroutine()

        return ApiKey.from(entity)
    }

    override suspend fun update(workspaceId: String, key: String, description: String?, status: ApiKeyStatus?): ApiKey {
        val entity = findApiKey(workspaceId = workspaceId, key = key)

        entity.patch(
            description = description,
            status = status,
        )

        reactiveCassandraOperations.batchOps()
            .upsert(entity)
            .upsert(ApiKeyEntity.from(entity))
            .executeCoroutine()

        return ApiKey.from(entity)
    }

    private suspend fun findApiKey(workspaceId: String, key: String): WorkspaceApiKey {
        return workspaceApiKeyCassandraRepository.findById(
            WorkspaceApiKeyPrimaryKey(
                workspaceId = workspaceId,
                apiKey = key,
            )
        )
            ?: throw ApiKeyNotExistsException(message = "워크스페이스($workspaceId)에 등록되지 않은 API-Key($key) 입니다")
    }

    override suspend fun findById(apiKey: String): ApiKey? {
        val entity = apiKeyCassandraRepository.findById(apiKey)
            ?: return null
        return ApiKey.from(entity)
    }

    override suspend fun existsById(workspaceId: String, apiKey: String): Boolean {
        return workspaceApiKeyCassandraRepository.existsById(
            WorkspaceApiKeyPrimaryKey(
                workspaceId = workspaceId,
                apiKey = apiKey,
            )
        )
    }

}
