package com.story.platform.core.domain.authentication

data class AuthenticationResponse(
    val workspaceId: String,
    val authenticationKey: String,
    val status: AuthenticationKeyStatus,
) {

    fun isActivated() = this.status.isActivated

    companion object {
        fun of(authenticationReverseKey: AuthenticationReverseKey) = AuthenticationResponse(
            workspaceId = authenticationReverseKey.workspaceId,
            authenticationKey = authenticationReverseKey.key.authenticationKey,
            status = authenticationReverseKey.status,
        )
    }

}
