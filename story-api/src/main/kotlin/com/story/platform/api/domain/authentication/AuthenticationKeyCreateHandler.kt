package com.story.platform.api.domain.authentication

import com.story.platform.api.domain.workspace.WorkspaceRetrieveHandler
import com.story.platform.core.common.spring.HandlerAdapter
import com.story.platform.core.domain.authentication.AuthenticationKeyCreator

@HandlerAdapter
class AuthenticationKeyCreateHandler(
    private val authenticationKeyCreator: AuthenticationKeyCreator,
    private val workspaceRetrieveHandler: WorkspaceRetrieveHandler,
) {

    suspend fun createAuthenticationKey(
        workspaceId: String,
        authenticationKey: String,
        description: String,
    ) {
        workspaceRetrieveHandler.validateEnabledWorkspace(workspaceId = workspaceId)
        authenticationKeyCreator.createAuthenticationKey(
            workspaceId = workspaceId,
            authenticationKey = authenticationKey,
            description = description,
        )
    }

}
