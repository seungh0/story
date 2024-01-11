package com.story.core.domain.authentication

data class AuthenticationResponse(
    val workspaceId: String,
    val authenticationKey: String,
    val status: AuthenticationStatus,
    val description: String,
) {

    fun isActivated() = this.status.isActivated

    companion object {
        fun of(workspaceAuthentication: WorkspaceAuthentication) = AuthenticationResponse(
            workspaceId = workspaceAuthentication.key.workspaceId,
            authenticationKey = workspaceAuthentication.key.authenticationKey,
            status = workspaceAuthentication.status,
            description = workspaceAuthentication.description,
        )

        fun of(authentication: Authentication) = AuthenticationResponse(
            workspaceId = authentication.workspaceId,
            authenticationKey = authentication.key.authenticationKey,
            status = authentication.status,
            description = authentication.description,
        )
    }

}
