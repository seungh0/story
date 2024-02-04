package com.story.api.application.apikey

import com.story.api.application.workspace.WorkspaceRetrieveHandler
import com.story.core.common.annotation.HandlerAdapter
import com.story.core.domain.apikey.ApiKeyCreator

@HandlerAdapter
class ApiKeyCreateHandler(
    private val apiKeyCreator: ApiKeyCreator,
    private val workspaceRetrieveHandler: WorkspaceRetrieveHandler,
) {

    suspend fun createApiKey(
        workspaceId: String,
        apiKey: String,
        description: String,
    ) {
        workspaceRetrieveHandler.validateEnabledWorkspace(workspaceId = workspaceId)
        apiKeyCreator.createApiKey(
            workspaceId = workspaceId,
            apiKey = apiKey,
            description = description,
        )
    }

}
