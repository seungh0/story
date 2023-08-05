package com.story.platform.core.domain.authentication

data class AuthenticationKeyResponse(
    val workspaceId: String,
    val authenticationKey: String,
    val status: AuthenticationKeyStatus,
    val description: String,
) {

    fun isActivated() = this.status.isActivated

    companion object {
        fun of(workspaceAuthenticationKey: WorkspaceAuthenticationKey) = AuthenticationKeyResponse(
            workspaceId = workspaceAuthenticationKey.key.workspaceId,
            authenticationKey = workspaceAuthenticationKey.key.authenticationKey,
            status = workspaceAuthenticationKey.status,
            description = workspaceAuthenticationKey.description,
        )

        fun of(authenticationKey: AuthenticationKey) = AuthenticationKeyResponse(
            workspaceId = authenticationKey.workspaceId,
            authenticationKey = authenticationKey.key.authenticationKey,
            status = authenticationKey.status,
            description = authenticationKey.description,
        )
    }

}
