package com.story.platform.api.application.authentication

import com.story.platform.api.application.workspace.WorkspaceApiResponse
import com.story.platform.core.domain.authentication.AuthenticationResponse
import com.story.platform.core.domain.authentication.AuthenticationStatus
import com.story.platform.core.domain.workspace.WorkspaceResponse

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
