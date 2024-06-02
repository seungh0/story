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
            workspaceApiKey: ApiKey,
        ) = EventRecord(
            eventAction = EventAction.MODIFIED,
            payload = ApiKeyEvent(
                workspaceId = workspaceApiKey.workspaceId,
                apiKey = workspaceApiKey.apiKey,
                status = workspaceApiKey.status,
            ),
            eventKey = ApiKeyEventKey(apiKey = workspaceApiKey.apiKey).makeKey(),
        )
    }

}
