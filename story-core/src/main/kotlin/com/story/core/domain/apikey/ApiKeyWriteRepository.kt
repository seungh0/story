package com.story.core.domain.apikey

interface ApiKeyWriteRepository {

    suspend fun create(workspaceId: String, key: String, description: String): ApiKey

    suspend fun update(workspaceId: String, key: String, description: String?, status: ApiKeyStatus?): ApiKey

}
