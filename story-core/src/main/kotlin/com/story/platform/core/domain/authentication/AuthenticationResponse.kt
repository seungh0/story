package com.story.platform.core.domain.authentication

data class AuthenticationResponse(
    val workspaceId: String,
    val apiKey: String,
    val status: AuthenticationKeyStatus,
) {

    fun isActivated() = this.status.isActivated

    companion object {
        fun of(authenticationReverseKey: AuthenticationReverseKey) = AuthenticationResponse(
            workspaceId = authenticationReverseKey.workspaceId,
            apiKey = authenticationReverseKey.key.apiKey,
            status = authenticationReverseKey.status,
        )
    }

}
