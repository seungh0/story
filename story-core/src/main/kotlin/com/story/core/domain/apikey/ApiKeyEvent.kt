package com.story.core.domain.apikey

import com.story.core.domain.event.EventAction
import com.story.core.domain.event.EventRecord

data class ApiKeyEvent(
    val workspaceId: String,
    val apiKey: String,
    val status: ApiKeyStatus,
) {

    companion object {
        fun updated(
            workspaceApiKey: WorkspaceApiKey,
        ) = EventRecord(
            eventAction = EventAction.UPDATED,
            payload = ApiKeyEvent(
                workspaceId = workspaceApiKey.key.workspaceId,
                apiKey = workspaceApiKey.key.apiKey,
                status = workspaceApiKey.status,
            ),
            eventKey = ApiKeyEventKey(apiKey = workspaceApiKey.key.apiKey).makeKey(),
        )
    }

}
