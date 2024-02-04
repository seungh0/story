package com.story.api.application.apikey

import com.story.api.application.workspace.WorkspaceApiResponse
import com.story.core.domain.apikey.ApiKeyResponse
import com.story.core.domain.apikey.ApiKeyStatus
import com.story.core.domain.workspace.WorkspaceResponse

data class ApiKeyApiResponse(
    val apiKey: String,
    val status: ApiKeyStatus,
    val description: String,
    val workspace: WorkspaceApiResponse,
) {

    companion object {
        fun of(
            apiKey: ApiKeyResponse,
            workspace: WorkspaceResponse,
        ) = ApiKeyApiResponse(
            apiKey = apiKey.apiKey,
            status = apiKey.status,
            description = apiKey.description,
            workspace = WorkspaceApiResponse.of(workspace = workspace),
        )
    }

}
