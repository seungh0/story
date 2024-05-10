package com.story.core.domain.apikey

import com.story.core.infrastructure.cassandra.executeCoroutine
import com.story.core.infrastructure.cassandra.upsert
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.stereotype.Service

@Service
class ApiKeyCreator(
    private val workspaceApiKeyRepository: WorkspaceApiKeyRepository,
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
) {

    suspend fun createApiKey(
        workspaceId: String,
        apiKey: String,
        description: String,
    ) {
        if (existsApiKey(workspaceId = workspaceId, apiKey = apiKey)) {
            throw ApiKeyAlreadyExistsException(message = "워크스페이스($workspaceId)에 이미 등록된 API-Key($apiKey)입니다")
        }

        val workspaceApiKey = WorkspaceApiKey.of(
            workspaceId = workspaceId,
            apiKey = apiKey,
            description = description,
        )

        reactiveCassandraOperations.batchOps()
            .upsert(workspaceApiKey)
            .upsert(ApiKeyEntity.from(workspaceApiKey))
            .executeCoroutine()
    }

    private suspend fun existsApiKey(
        workspaceId: String,
        apiKey: String,
    ): Boolean {
        return workspaceApiKeyRepository.existsById(
            WorkspaceApiKeyPrimaryKey(
                workspaceId = workspaceId,
                apiKey = apiKey,
            )
        )
    }

}
