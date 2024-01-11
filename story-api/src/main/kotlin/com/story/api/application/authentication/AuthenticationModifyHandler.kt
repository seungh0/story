package com.story.api.application.authentication

import com.story.core.common.annotation.HandlerAdapter
import com.story.core.domain.authentication.AuthenticationModifier
import com.story.core.domain.authentication.AuthenticationStatus

@HandlerAdapter
class AuthenticationModifyHandler(
    private val authenticationModifier: AuthenticationModifier,
) {

    suspend fun patchAuthentication(
        workspaceId: String,
        authenticationKey: String,
        description: String?,
        status: AuthenticationStatus?,
    ) {
        authenticationModifier.patchAuthentication(
            workspaceId = workspaceId,
            authenticationKey = authenticationKey,
            description = description,
            status = status,
        )
    }

}
