package com.story.platform.api.domain.workspace

import com.story.platform.core.common.annotation.HandlerAdapter
import com.story.platform.core.domain.workspace.WorkspaceEventProducer
import com.story.platform.core.domain.workspace.WorkspaceRemover

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
