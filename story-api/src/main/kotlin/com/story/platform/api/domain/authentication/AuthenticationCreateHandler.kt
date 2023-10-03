package com.story.platform.api.domain.authentication

import com.story.platform.api.domain.workspace.WorkspaceRetrieveHandler
import com.story.platform.core.common.spring.HandlerAdapter
import com.story.platform.core.domain.authentication.AuthenticationCreator

@HandlerAdapter
class AuthenticationCreateHandler(
    private val authenticationCreator: AuthenticationCreator,
    private val workspaceRetrieveHandler: WorkspaceRetrieveHandler,
) {

    suspend fun createAuthentication(
        workspaceId: String,
        authenticationKey: String,
        description: String,
    ) {
        workspaceRetrieveHandler.validateEnabledWorkspace(workspaceId = workspaceId)
        authenticationCreator.createAuthentication(
            workspaceId = workspaceId,
            authenticationKey = authenticationKey,
            description = description,
        )
    }

}
