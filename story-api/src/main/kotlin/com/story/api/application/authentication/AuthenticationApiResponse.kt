package com.story.api.application.authentication

import com.story.api.application.workspace.WorkspaceApiResponse
import com.story.core.domain.authentication.AuthenticationResponse
import com.story.core.domain.authentication.AuthenticationStatus
import com.story.core.domain.workspace.WorkspaceResponse

data class AuthenticationApiResponse(
    val authenticationKey: String,
    val status: AuthenticationStatus,
    val description: String,
    val workspace: WorkspaceApiResponse,
) {

    companion object {
        fun of(
            authenticationKey: AuthenticationResponse,
            workspace: WorkspaceResponse,
        ) = AuthenticationApiResponse(
            authenticationKey = authenticationKey.authenticationKey,
            status = authenticationKey.status,
            description = authenticationKey.description,
            workspace = WorkspaceApiResponse.of(workspace = workspace),
        )
    }

}
