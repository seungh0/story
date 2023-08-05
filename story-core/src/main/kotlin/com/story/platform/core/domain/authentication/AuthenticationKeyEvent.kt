package com.story.platform.core.domain.authentication

import com.story.platform.core.domain.event.EventAction
import com.story.platform.core.domain.event.EventKeyGenerator
import com.story.platform.core.domain.event.EventRecord

data class AuthenticationKeyEvent(
    val workspaceId: String,
    val authenticationKey: String,
    val status: AuthenticationKeyStatus,
) {

    companion object {
        fun updated(
            workspaceAuthenticationKey: WorkspaceAuthenticationKey,
        ) = EventRecord(
            eventAction = EventAction.UPDATED,
            payload = AuthenticationKeyEvent(
                workspaceId = workspaceAuthenticationKey.key.workspaceId,
                authenticationKey = workspaceAuthenticationKey.key.authenticationKey,
                status = workspaceAuthenticationKey.status,
            ),
            eventKey = EventKeyGenerator.authenticationKey(authenticationKey = workspaceAuthenticationKey.key.authenticationKey),
        )
    }

}
