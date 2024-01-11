package com.story.api.application.workspace

import com.story.core.common.annotation.HandlerAdapter
import com.story.core.domain.workspace.WorkspaceEventProducer
import com.story.core.domain.workspace.WorkspaceRemover

@HandlerAdapter
class WorkspaceRemoveHandler(
    private val workspaceRemover: WorkspaceRemover,
    private val workspaceEventProducer: WorkspaceEventProducer,
) {

    suspend fun removeWorkspace(
        workspaceId: String,
    ) {
        workspaceRemover.removeWorkspace(workspaceId = workspaceId)
        workspaceEventProducer.publishDeletedEvent(
            workspaceId = workspaceId
        )
    }

}
