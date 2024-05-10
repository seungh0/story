package com.story.api.application.apikey

import com.story.api.application.workspace.WorkspaceApiResponse
import com.story.core.domain.apikey.ApiKey
import com.story.core.domain.apikey.ApiKeyStatus
import com.story.core.domain.workspace.Workspace

data class ApiKeyApiResponse(
    val status: ApiKeyStatus,
    val description: String,
    val workspace: WorkspaceApiResponse,
) {

    companion object {
        fun of(
            apiKey: ApiKey,
            workspace: Workspace,
        ) = ApiKeyApiResponse(
            status = apiKey.status,
            description = apiKey.description,
            workspace = WorkspaceApiResponse.of(workspace = workspace),
        )
    }

}
