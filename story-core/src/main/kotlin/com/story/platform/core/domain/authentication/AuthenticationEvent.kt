package com.story.platform.core.domain.authentication

import com.story.platform.core.domain.event.EventAction
import com.story.platform.core.domain.event.EventKeyGenerator
import com.story.platform.core.domain.event.EventRecord

data class AuthenticationEvent(
    val workspaceId: String,
    val authenticationKey: String,
    val status: AuthenticationStatus,
) {

    companion object {
        fun updated(
            workspaceAuthentication: WorkspaceAuthentication,
        ) = EventRecord(
            eventAction = EventAction.UPDATED,
            payload = AuthenticationEvent(
                workspaceId = workspaceAuthentication.key.workspaceId,
                authenticationKey = workspaceAuthentication.key.authenticationKey,
                status = workspaceAuthentication.status,
            ),
            eventKey = EventKeyGenerator.authenticationKey(authenticationKey = workspaceAuthentication.key.authenticationKey),
        )
    }

}
