package com.story.api.application.apikey

import com.story.core.common.annotation.HandlerAdapter
import com.story.core.domain.apikey.ApiKeyInvalidException
import com.story.core.domain.apikey.ApiKeyNotExistsException
import com.story.core.domain.apikey.ApiKeyReaderWithCache
import com.story.core.domain.apikey.ApiKeyStatus
import com.story.core.domain.workspace.WorkspaceNotExistsException
import com.story.core.domain.workspace.WorkspaceReaderWithCache

@HandlerAdapter
class ApiKeyRetrieveHandler(
    private val apiKeyReaderWithCache: ApiKeyReaderWithCache,
    private val workspaceReaderWithCache: WorkspaceReaderWithCache,
) {

    suspend fun getApiKey(
        requestApiKey: String,
        filterStatus: ApiKeyStatus?,
    ): ApiKeyResponse {
        val apiKey = apiKeyReaderWithCache.getApiKey(apiKey = requestApiKey)
            .orElseThrow { ApiKeyNotExistsException("존재하지 않는 ApiKey($requestApiKey)입니다") }

        if (filterStatus != null && apiKey.status != filterStatus) {
            throw ApiKeyInvalidException(message = "요청한 상태($filterStatus)가 아닌 API 키($requestApiKey) 입니다. 현재 상태: ${apiKey.status}")
        }
        val workspace = workspaceReaderWithCache.getWorkspace(workspaceId = apiKey.workspaceId)
            .orElseThrow { WorkspaceNotExistsException(message = "워크스페이스(${apiKey.workspaceId})가 존재하지 않습니다") }

        return ApiKeyResponse.of(apiKey = apiKey, workspace = workspace)
    }

}
