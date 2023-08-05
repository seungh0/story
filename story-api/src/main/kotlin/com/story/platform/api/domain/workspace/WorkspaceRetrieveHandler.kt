package com.story.platform.api.domain.workspace

import com.story.platform.core.domain.workspace.WorkspaceRetriever
import org.springframework.stereotype.Service

@Service
class WorkspaceRetrieveHandler(
    private val workspaceRetriever: WorkspaceRetriever,
) {

    suspend fun validateEnabledWorkspace(
        workspaceId: String,
    ) {
        workspaceRetriever.getWorkspace(workspaceId = workspaceId)
    }

}
