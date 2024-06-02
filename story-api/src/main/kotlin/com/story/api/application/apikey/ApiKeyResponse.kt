package com.story.api.application.apikey

import com.story.api.application.workspace.WorkspaceResponse
import com.story.core.domain.apikey.ApiKey
import com.story.core.domain.apikey.ApiKeyStatus
import com.story.core.domain.workspace.Workspace

data class ApiKeyResponse(
    val status: ApiKeyStatus,
    val description: String,
    val workspace: WorkspaceResponse,
) {

    companion object {
        fun of(
            apiKey: ApiKey,
            workspace: Workspace,
        ) = ApiKeyResponse(
            status = apiKey.status,
            description = apiKey.description,
            workspace = WorkspaceResponse.of(workspace = workspace),
        )
    }

}
