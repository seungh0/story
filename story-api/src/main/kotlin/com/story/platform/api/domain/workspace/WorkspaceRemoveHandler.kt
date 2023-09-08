package com.story.platform.api.domain.workspace

import com.story.platform.core.common.spring.HandlerAdapter
import com.story.platform.core.domain.workspace.WorkspaceEventProducer
import com.story.platform.core.domain.workspace.WorkspaceRemover

@HandlerAdapter
class WorkspaceRemoveHandler(
    private val workspaceRemover: WorkspaceRemover,
    private val workspaceEventProducer: WorkspaceEventProducer,
) {

    suspend fun remove(
        workspaceId: String,
    ) {
        workspaceRemover.remove(workspaceId = workspaceId)
        workspaceEventProducer.publishDeletedEvent(
            workspaceId = workspaceId
        )
    }

}
