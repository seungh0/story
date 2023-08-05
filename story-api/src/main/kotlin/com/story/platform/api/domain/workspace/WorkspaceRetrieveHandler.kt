package com.story.platform.api.domain.workspace

import com.story.platform.core.common.spring.HandlerAdapter
import com.story.platform.core.domain.workspace.WorkspaceRetriever

@HandlerAdapter
class WorkspaceRetrieveHandler(
    private val workspaceRetriever: WorkspaceRetriever,
) {

    suspend fun validateEnabledWorkspace(
        workspaceId: String,
    ) {
        workspaceRetriever.getWorkspace(workspaceId = workspaceId)
    }

}
