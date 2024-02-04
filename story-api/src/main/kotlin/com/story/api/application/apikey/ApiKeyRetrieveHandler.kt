package com.story.api.application.apikey

import com.story.core.common.annotation.HandlerAdapter
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
        key: String,
        filterStatus: ApiKeyStatus?,
    ): ApiKeyApiResponse {
        val apiKey = apiKeyRetriever.getApiKey(apiKey = key)
        if (filterStatus != null && apiKey.status != filterStatus) {
            throw ApiKeyNotExistsException(message = "요청한 상태($filterStatus)가 아닌 API 키($key) 입니다. 현재 상태: ${apiKey.status}")
        }
        val workspace = workspaceRetriever.getWorkspace(workspaceId = apiKey.workspaceId)
        return ApiKeyApiResponse.of(apiKey = apiKey, workspace = workspace)
    }

}
