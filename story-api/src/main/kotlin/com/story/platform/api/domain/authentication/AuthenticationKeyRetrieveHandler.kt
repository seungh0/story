package com.story.platform.api.domain.authentication

import com.story.platform.core.common.spring.HandlerAdapter
import com.story.platform.core.domain.authentication.AuthenticationKeyRetriever
import com.story.platform.core.domain.workspace.WorkspaceRetriever

@HandlerAdapter
class AuthenticationKeyRetrieveHandler(
    private val authenticationKeyManager: AuthenticationKeyRetriever,
    private val workspaceRetriever: WorkspaceRetriever,
) {

    suspend fun getAuthenticationKey(
        apiKey: String,
    ): AuthenticationKeyApiResponse {
        val authentication = authenticationKeyManager.getAuthenticationKey(authenticationKey = apiKey)
        val workspace = workspaceRetriever.getWorkspace(authentication.workspaceId)
        return AuthenticationKeyApiResponse.of(authenticationKey = authentication, workspace = workspace)
    }

}
