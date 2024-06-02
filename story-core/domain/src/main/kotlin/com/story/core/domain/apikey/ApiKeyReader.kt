package com.story.core.domain.apikey

import org.springframework.stereotype.Service

@Service
class ApiKeyReader(
    private val apiKeyReadRepository: ApiKeyReadRepository,
) {

    suspend fun getApiKey(
        apiKey: String,
    ): ApiKey? {
        return apiKeyReadRepository.findById(apiKey)
    }

}
