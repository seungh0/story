package com.story.core.domain.workspace

import com.story.core.domain.event.BaseEvent
import com.story.core.domain.event.EventAction
import com.story.core.domain.event.EventKeyGenerator
import com.story.core.domain.event.EventRecord

data class WorkspaceEvent(
    val workspaceId: String,
) : BaseEvent {

    companion object {
        fun deleted(
            workspaceId: String,
        ) = EventRecord(
            eventAction = EventAction.DELETED,
            eventKey = EventKeyGenerator.workspace(workspaceId = workspaceId),
            payload = WorkspaceEvent(
                workspaceId = workspaceId,
            )
        )
    }

}
