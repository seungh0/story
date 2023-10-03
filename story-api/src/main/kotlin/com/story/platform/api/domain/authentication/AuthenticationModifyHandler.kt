package com.story.platform.api.domain.authentication

import com.story.platform.core.common.annotation.HandlerAdapter
import com.story.platform.core.domain.authentication.AuthenticationModifier
import com.story.platform.core.domain.authentication.AuthenticationStatus

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
