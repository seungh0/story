package com.story.core.domain.apikey

import org.springframework.stereotype.Service

@Service
class ApiKeyCreator(
    private val apiKeyReadRepository: ApiKeyReadRepository,
    private val apiKeyWriteRepository: ApiKeyWriteRepository,
) {

    suspend fun createApiKey(
        workspaceId: String,
        apiKey: String,
        description: String,
    ) {
        if (existsApiKey(workspaceId = workspaceId, apiKey = apiKey)) {
            throw ApiKeyAlreadyExistsException(message = "워크스페이스($workspaceId)에 이미 등록된 API-Key($apiKey)입니다")
        }

        apiKeyWriteRepository.create(
            workspaceId = workspaceId,
            key = apiKey,
            description = description,
        )
    }

    private suspend fun existsApiKey(
        workspaceId: String,
        apiKey: String,
    ): Boolean {
        return apiKeyReadRepository.existsById(
            workspaceId = workspaceId,
            apiKey = apiKey,
        )
    }

}
