package com.story.platform.api.domain.authentication

import com.story.platform.core.common.spring.HandlerAdapter
import com.story.platform.core.domain.authentication.AuthenticationRetriever
import com.story.platform.core.domain.workspace.WorkspaceRetriever

@HandlerAdapter
class AuthenticationRetrieveHandler(
    private val authenticationKeyManager: AuthenticationRetriever,
    private val workspaceRetriever: WorkspaceRetriever,
) {

    suspend fun getAuthenticationKey(
        apiKey: String,
    ): AuthenticationApiResponse {
        val authentication = authenticationKeyManager.getAuthenticationKey(authenticationKey = apiKey)
        val workspace = workspaceRetriever.getWorkspace(authentication.workspaceId)
        return AuthenticationApiResponse.of(authenticationKey = authentication, workspace = workspace)
    }

}
