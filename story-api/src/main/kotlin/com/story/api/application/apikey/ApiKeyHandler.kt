package com.story.api.application.apikey

import com.story.core.common.annotation.HandlerAdapter
import com.story.core.common.http.HttpHeader
import com.story.core.common.http.getApiKey
import com.story.core.domain.apikey.ApiKey
import com.story.core.domain.apikey.ApiKeyEmptyException
import com.story.core.domain.apikey.ApiKeyInactivatedException
import com.story.core.domain.apikey.ApiKeyInvalidException
import com.story.core.domain.apikey.ApiKeyReaderWithCache
import org.springframework.web.server.ServerWebExchange

@HandlerAdapter
class ApiKeyHandler(
    private val apiKeyReaderWithCache: ApiKeyReaderWithCache,
) {

    suspend fun handleApiKey(
        serverWebExchange: ServerWebExchange,
        allowedDisabledApiKey: Boolean = false,
    ): ApiKey {
        val requestApiKey = serverWebExchange.getApiKey()
            ?: throw ApiKeyEmptyException("API Key 헤더(${HttpHeader.X_STORY_API_KEY.header})가 비어있습니다")

        val apiKey = apiKeyReaderWithCache.getApiKey(apiKey = requestApiKey)
            .orElseThrow { ApiKeyInvalidException("등록되지 않은 ApiKey($requestApiKey)입니다") }

        if (!allowedDisabledApiKey && !apiKey.isActivated()) {
            throw ApiKeyInactivatedException("비활성화된 ApiKey($requestApiKey)입니다. [워크스페이스(${apiKey.workspaceId}) 현재 상태: ${apiKey.status}]")
        }
        return apiKey
    }

}
