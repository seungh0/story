package com.story.api.application.apikey

import com.story.core.common.annotation.HandlerAdapter
import com.story.core.domain.apikey.ApiKeyInvalidException
import com.story.core.domain.apikey.ApiKeyNotExistsException
import com.story.core.domain.apikey.ApiKeyRetriever
import com.story.core.domain.apikey.ApiKeyStatus
import com.story.core.domain.workspace.WorkspaceRetriever

@HandlerAdapter
class ApiKeyRetrieveHandler(
    private val apiKeyRetriever: ApiKeyRetriever,
    private val workspaceRetriever: WorkspaceRetriever,
) {

    suspend fun getApiKey(
        requestApiKey: String,
        filterStatus: ApiKeyStatus?,
    ): ApiKeyApiResponse {
        val apiKey = apiKeyRetriever.getApiKey(apiKey = requestApiKey)
        if (apiKey.isNotFound()) {
            throw ApiKeyNotExistsException("존재하지 않는 ApiKey($requestApiKey)입니다")
        }

        if (filterStatus != null && apiKey.status != filterStatus) {
            throw ApiKeyInvalidException(message = "요청한 상태($filterStatus)가 아닌 API 키($requestApiKey) 입니다. 현재 상태: ${apiKey.status}")
        }
        val workspace = workspaceRetriever.getWorkspace(workspaceId = apiKey.workspaceId)
        return ApiKeyApiResponse.of(apiKey = apiKey, workspace = workspace)
    }

}
