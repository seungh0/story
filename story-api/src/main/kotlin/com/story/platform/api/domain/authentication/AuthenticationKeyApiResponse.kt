package com.story.platform.api.domain.authentication

import com.story.platform.api.domain.workspace.WorkspaceApiResponse
import com.story.platform.core.domain.authentication.AuthenticationKeyResponse
import com.story.platform.core.domain.authentication.AuthenticationKeyStatus
import com.story.platform.core.domain.workspace.WorkspaceResponse

data class AuthenticationKeyApiResponse(
    val apiKey: String,
    val status: AuthenticationKeyStatus,
    val description: String,
    val workspace: WorkspaceApiResponse,
) {

    companion object {
        fun of(
            authenticationKey: AuthenticationKeyResponse,
            workspace: WorkspaceResponse,
        ) = AuthenticationKeyApiResponse(
            apiKey = authenticationKey.authenticationKey,
            status = authenticationKey.status,
            description = authenticationKey.description,
            workspace = WorkspaceApiResponse.of(workspace = workspace),
        )
    }

}
