package com.story.platform.core.domain.authentication

data class AuthenticationResponse(
    val workspaceId: String,
    val authenticationKey: String,
    val status: AuthenticationKeyStatus,
    val description: String,
) {

    fun isActivated() = this.status.isActivated

    companion object {
        fun of(authenticationKey: AuthenticationKey) = AuthenticationResponse(
            workspaceId = authenticationKey.key.workspaceId,
            authenticationKey = authenticationKey.key.authenticationKey,
            status = authenticationKey.status,
            description = authenticationKey.description,
        )

        fun of(authenticationReverseKey: AuthenticationReverseKey) = AuthenticationResponse(
            workspaceId = authenticationReverseKey.workspaceId,
            authenticationKey = authenticationReverseKey.key.authenticationKey,
            status = authenticationReverseKey.status,
            description = authenticationReverseKey.description,
        )
    }

}
