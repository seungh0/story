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
            authenticationKey: AuthenticationKey,
        ) = EventRecord(
            eventAction = EventAction.UPDATED,
            payload = AuthenticationKeyEvent(
                workspaceId = authenticationKey.key.workspaceId,
                authenticationKey = authenticationKey.key.authenticationKey,
                status = authenticationKey.status,
            ),
            eventKey = EventKeyGenerator.authenticationKey(authenticationKey = authenticationKey.key.authenticationKey),
        )
    }

}
