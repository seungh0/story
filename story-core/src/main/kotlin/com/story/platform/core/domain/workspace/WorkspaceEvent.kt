package com.story.platform.core.domain.workspace

import com.story.platform.core.domain.event.BaseEvent
import com.story.platform.core.domain.event.EventAction
import com.story.platform.core.domain.event.EventKeyGenerator
import com.story.platform.core.domain.event.EventRecord

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
