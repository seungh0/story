package com.story.core.domain.apikey

interface ApiKeyReadRepository {

    suspend fun findById(apiKey: String): ApiKey?

    suspend fun existsById(workspaceId: String, apiKey: String): Boolean

}
